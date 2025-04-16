package repository.mapping

import slick.lifted.{ProvenShape, Tag}

import java.time.LocalDateTime
import repository.ExtendedPostgresProfile.api._
import repository.dto
import repository.dto.AccountsDTO

class AccountsTable(tag: Tag) extends Table[AccountsDTO](tag, Some("marlow"), "accounts") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[Option[String]] = column[Option[String]]("name")

  def phoneNumber: Rep[Option[String]] = column[Option[String]]("phone_number")

  def password: Rep[Option[String]] = column[Option[String]]("password")

  def balance: Rep[Option[BigDecimal]] = column[Option[BigDecimal]]("balance")

  def version: Rep[Option[Int]] = column[Option[Int]]("version")

  def createdAt: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("created_at")

  def updatedAt: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("updated_at")

  def * : ProvenShape[AccountsDTO] = (id, name, phoneNumber, balance, password, version, createdAt, updatedAt) <> ((dto.AccountsDTO.apply _).tupled, AccountsDTO.unapply)
}
