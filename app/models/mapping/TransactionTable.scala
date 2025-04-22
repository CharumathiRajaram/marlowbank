package models.mapping

import models.dto.TransactionDTO
import slick.lifted.{ProvenShape, Tag}
import repository.ExtendedPostgresProfile.api._

import java.time.LocalDateTime

import java.util.UUID

class TransactionTable(tag: Tag) extends Table[TransactionDTO](tag, Some("marlow"), "transaction") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def transactionId: Rep[Option[UUID]] = column[Option[UUID]]("transaction_id")

  def accountId: Rep[Long] = column[Long]("account_id")

  def operation: Rep[String] = column[String]("operation")

  def amount: Rep[BigDecimal] = column[BigDecimal]("amount")

  def transactionTime: Rep[LocalDateTime] = column[LocalDateTime]("transaction_time")

  def balanceAfter: Rep[BigDecimal] = column[BigDecimal]("balance_after")

  override def * : ProvenShape[TransactionDTO] = (id, transactionId, accountId, operation, amount, transactionTime, balanceAfter) <> ((TransactionDTO.apply _).tupled, TransactionDTO.unapply)
}