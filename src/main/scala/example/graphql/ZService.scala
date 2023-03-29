package example.graphql

import example.schema.AccountEvent._
import example.schema.{ Account, AccountEvent }
import zio.stream.ZStream
import zio.{ Hub, Ref, UIO, URIO, ZIO, ZLayer }

import java.time.ZonedDateTime

object ZService {

  trait ExampleService {

    def addAccount(name: String, balance: Float): UIO[Boolean]

    def debitAccount(account: Int, debitAmount: Float): UIO[Boolean]

    def creditAccount(account: Int, creditAmount: Float): UIO[Boolean]

    def getAccount(account: Int): UIO[List[Account]]

    def findAccountHolder(name: String): UIO[List[Account]]

    def deleteAccount(account: Int): UIO[Boolean]

    def accountEvent: ZStream[Any, Nothing, AccountEvent]
  }

  def addAccount(name: String, balance: Float): URIO[ExampleService, Boolean] =
    ZIO.serviceWithZIO(_.addAccount(name, balance))

  def debitAccount(account: Int, debitAmount: Float): URIO[ExampleService, Boolean] =
    ZIO.serviceWithZIO(_.debitAccount(account, debitAmount))

  def creditAccount(account: Int, creditAmount: Float): URIO[ExampleService, Boolean] =
    ZIO.serviceWithZIO(_.creditAccount(account, creditAmount))

  def getAccount(account: Int): URIO[ExampleService, List[Account]] =
    ZIO.serviceWithZIO(_.getAccount(account))

  def findAccountHolder(name: String): URIO[ExampleService, List[Account]] =
    ZIO.serviceWithZIO(_.findAccountHolder(name))

  def deleteAccount(account: Int): URIO[ExampleService, Boolean] =
    ZIO.serviceWithZIO(_.deleteAccount(account))

  def deletedEvents: ZStream[ExampleService, Nothing, AccountEvent] =
    ZStream.serviceWithStream(_.accountEvent)

  def make(account: Map[Int, Account]): ZLayer[Any, Nothing, ExampleService] = ZLayer {
    for {
      accountState   <- Ref.make(account)
      accountCounter <- Ref.make(0)
      subscribers    <- Hub.unbounded[AccountEvent]
    } yield new ExampleService {

      override def addAccount(name: String, balance: Float): UIO[Boolean] = for {
        account <- accountCounter.getAndUpdate(_ + 1)
        _ <- accountState
          .getAndUpdate { state =>
            state + (account -> Account(name, account, balance, ZonedDateTime.now()))
          }
          .tap(_ => subscribers.publish(OPEN))
      } yield true

      override def debitAccount(account: Int, debitAmount: Float): UIO[Boolean] =
        accountState
          .modify(state =>
            if (state(account).accountNumber == account)
              (true, state + (account -> state(account).copy(balance = state(account).balance + debitAmount)))
            else (false, state)
          )
          .tap(debited => ZIO.when(debited)(subscribers.publish(DEBIT)))

      override def creditAccount(account: Int, creditAmount: Float): UIO[Boolean] =
        accountState
          .modify(state =>
            if (state(account).accountNumber == account)
              (true, state + (account -> state(account).copy(balance = state(account).balance - creditAmount)))
            else (false, state)
          )
          .tap(credited => ZIO.when(credited)(subscribers.publish(CREDIT)))

      override def getAccount(accountNumber: Int): UIO[List[Account]] =
        accountState.get
          .map(_.values.collect {
            case r @ Account(_, a, _, _) if a == accountNumber => r
          }.toList)
          .tap(list => ZIO.when(list.nonEmpty)(subscribers.publish(READ)))

      override def findAccountHolder(name: String): UIO[List[Account]] =
        accountState.get
          .map(_.values.collect {
            case r @ Account(n, _, _, _) if n == name => r
          }.toList)
          .tap(list => ZIO.when(list.nonEmpty)(subscribers.publish(READ)))

      override def deleteAccount(account: Int): UIO[Boolean] =
        accountState
          .modify(state =>
            if (state(account).accountNumber == account)
              (true, state.removed(account))
            else (false, state)
          )
          .tap(deleted => ZIO.when(deleted)(subscribers.publish(DELETE)))

      override def accountEvent: ZStream[Any, Nothing, AccountEvent] =
        ZStream.scoped(subscribers.subscribe).flatMap(ZStream.fromQueue(_))
    }
  }
}
