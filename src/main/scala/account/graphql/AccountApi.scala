package account.graphql

import caliban.GraphQL.graphQL
import caliban.schema.Annotations.GQLDeprecated
import caliban.schema.GenericSchema
import caliban.{ GraphQL, RootResolver }
import account.graphql.ZAccountService.AccountService
import account.schema.{ Account, AccountEvent }
import zio.URIO
import zio.stream.ZStream

object AccountApi extends GenericSchema[AccountEnv] {

  case class AddAccountArgs(name: String, balance: Long)

  case class FindByAccountArgs(account: Int)

  case class FindByUserArgs(name: String)

  case class AccountBalanceUpdateArgs(account: Int, amount: Float)

  case class DeleteAccountArgs(account: Int)

  case class Queries(
    account: FindByAccountArgs => URIO[AccountService, List[Account]],
    @GQLDeprecated("This field will be deprecated Q4/2023")
    accountHolder: FindByUserArgs => URIO[AccountService, List[Account]]
  )

  case class Mutations(
    addAccount: AddAccountArgs => URIO[AccountService, Boolean],
    creditAccount: AccountBalanceUpdateArgs => URIO[AccountService, Boolean],
    debitAccount: AccountBalanceUpdateArgs => URIO[AccountService, Boolean],
    deleteAccount: DeleteAccountArgs => URIO[AccountService, Boolean]
  )

  case class Subscriptions(characterDeleted: ZStream[AccountService, Nothing, AccountEvent])

  val api: GraphQL[AccountService] =
    graphQL(
      RootResolver(
        Queries(
          account = args => ZAccountService.getAccount(args.account),
          accountHolder = args => ZAccountService.findAccountHolder(args.name)
        ),
        Mutations(
          addAccount = args => ZAccountService.addAccount(args.name, args.balance),
          creditAccount = args => ZAccountService.creditAccount(args.account, args.amount),
          debitAccount = args => ZAccountService.debitAccount(args.account, args.amount),
          deleteAccount = args => ZAccountService.deleteAccount(args.account)
        ),
        Subscriptions(ZAccountService.deletedEvents)
      )
    )
}
