package example

import caliban.GraphQL.graphQL
import caliban._
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.GenericSchema
import example.graphql.{ ExampleEnv, ZService }
import example.graphql.ZService.ExampleService
import example.schema.{ Account, AccountEvent }
//import caliban.schema.ArgBuilder.auto._
import zio._
import zio.stream.ZStream

import scala.language.postfixOps

object ExampleApi extends GenericSchema[ExampleEnv] {

//  import auto._

  case class AddAccountArgs(name: String, balance: Long)
  case class FindByAccountArgs(account: Int)
  case class FindByUserArgs(name: String)
  case class AccountBalanceUpdateArgs(account: Int, amount: Float)
  case class DeleteAccountArgs(account: Int)

  case class Queries(
    account: FindByAccountArgs => URIO[ExampleService, List[Account]],
    @GQLDeprecated("This field will be deprecated Q4/2023")
    accountHolder: FindByUserArgs => URIO[ExampleService, List[Account]]
  )

  case class Mutations(
    addAccount: AddAccountArgs => URIO[ExampleService, Boolean],
    creditAccount: AccountBalanceUpdateArgs => URIO[ExampleService, Boolean],
    debitAccount: AccountBalanceUpdateArgs => URIO[ExampleService, Boolean],
    deleteAccount: DeleteAccountArgs => URIO[ExampleService, Boolean]
  )

  case class Subscriptions(characterDeleted: ZStream[ExampleService, Nothing, AccountEvent])

  val api: GraphQL[ExampleService] =
    graphQL(
      RootResolver(
        Queries(
          account = args => ZService.getAccount(args.account),
          accountHolder = args => ZService.findAccountHolder(args.name)
        ),
        Mutations(
          addAccount = args => ZService.addAccount(args.name, args.balance),
          creditAccount = args => ZService.creditAccount(args.account, args.amount),
          debitAccount = args => ZService.debitAccount(args.account, args.amount),
          deleteAccount = args => ZService.deleteAccount(args.account)
        ),
        Subscriptions(ZService.deletedEvents)
      )
    )
}
