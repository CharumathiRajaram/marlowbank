package repository.dto

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import java.util.UUID

case class TransactionResponseDTO(
                                   id: Long,
                                   transactionId: Option[UUID],
                                   accountId: Long,
                                   operation: String,
                                   amount: BigDecimal,
                                   transactionTime: LocalDateTime,
                                   balanceAfter: BigDecimal
                                 )

object TransactionResponseDTO {
  implicit val dataFormat: OFormat[TransactionResponseDTO] = Json.format[TransactionResponseDTO]
  implicit val empty: TransactionResponseDTO = this (0, None, 0, "", BigDecimal(0), LocalDateTime.now(), BigDecimal(0))

}
