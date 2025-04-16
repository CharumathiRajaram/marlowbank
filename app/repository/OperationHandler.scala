package repository

import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import utils.Logging
import repository.ExtendedPostgresProfile.api._
import repository.dto.{AccountsDTO, TransactionDTO}
import repository.mapping.{AccountsTable, TransactionTable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class OperationHandler(datastore: Database) extends Logging {
  private val transactionTable = TableQuery[TransactionTable]
  private val accountsTable = TableQuery[AccountsTable]

  def fetchAccount(id: Long): Option[AccountsDTO] = {
    val passwordQuery = accountsTable.filter(acc => acc.id === id).result
    try {
      Await.result(datastore.run(passwordQuery), Duration.Inf).headOption
    }
    catch {
      case err: Throwable =>
        logger.error(s"database error: exception while reading transaction. ${err.getMessage}")
        Some(AccountsDTO.empty)
    }
  }

  def updateTransaction(transaction: TransactionDTO): Long = {
    logger.info("updating transaction log", transaction)
    val updateQuery = transactionTable returning transactionTable.map(_.id) += transaction
    try {
      Await.result(datastore.run(updateQuery), Duration.Inf)
    } catch {
      case _: Throwable =>
        logger.error(s"database error: exception during updating transaction")
        0L
    }
  }

  def updateAccount(accID: Long, balance: BigDecimal, version: Int): Int = {
    logger.info("updating account balance", accID, balance, version)
    val updateAccount = accountsTable.filter(acc => acc.id === accID)
    try {
      Await.result(datastore.run(updateAccount.map(acc => (acc.balance, acc.version)).update((Some(balance), Some(version)))), Duration.Inf)
    } catch {
      case _: Throwable =>
        logger.error(s"database error: exception during updating balance")
        0
    }
  }
}
