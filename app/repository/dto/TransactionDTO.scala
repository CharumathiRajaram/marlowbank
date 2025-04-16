package repository.dto

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import java.util.UUID

case class TransactionDTO(
                                   id: Long,
                                   transactionId: Option[UUID],
                                   accountId: Long,
                                   operation: String,
                                   amount: BigDecimal,
                                   transactionTime: LocalDateTime,
                                   balanceAfter: BigDecimal
                                 )

object TransactionDTO {
  implicit val dataFormat: OFormat[TransactionDTO] = Json.format[TransactionDTO]
  implicit val empty: TransactionDTO = this (0, None, 0, "", BigDecimal(0), LocalDateTime.now(), BigDecimal(0))

}
