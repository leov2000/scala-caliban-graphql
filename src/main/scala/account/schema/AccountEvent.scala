package account.schema

import caliban.schema.Annotations.GQLDescription

@GQLDescription("Notifies the branch manager of updates")
sealed trait AccountEvent

object AccountEvent {
  case object DEBIT extends AccountEvent
  case object CREDIT extends AccountEvent
}
