package account.schema

sealed trait AccountEvent
// comment field here
object AccountEvent {
  case object DEBIT extends AccountEvent
  case object CREDIT extends AccountEvent
}
