package repository.dto

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class AccountsDTO(id: Long, name: Option[String], phoneNumber: Option[String], balance: Option[BigDecimal], password: Option[String], version: Option[Int], createdAt: Option[LocalDateTime], updatedAt: Option[LocalDateTime])

object AccountsDTO {
  implicit val dataFormat: OFormat[AccountsDTO] = Json.format[AccountsDTO]
  implicit val empty: AccountsDTO = this (0L, None, None, None, None, None, None, None)

}
