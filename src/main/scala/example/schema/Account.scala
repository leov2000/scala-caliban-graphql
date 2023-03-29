package example.schema

import java.time.ZonedDateTime

case class Account(name: String, accountNumber: Int, balance: Float, openedOn: ZonedDateTime)
