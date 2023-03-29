package example.schema

sealed trait AccountEvent

object AccountEvent {
  case object OPEN extends AccountEvent
  case object DELETE extends AccountEvent
  case object DEBIT extends AccountEvent
  case object CREDIT extends AccountEvent
  case object READ extends AccountEvent
}
