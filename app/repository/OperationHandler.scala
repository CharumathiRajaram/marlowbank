package repository

import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import utils.Logging
import repository.ExtendedPostgresProfile.api._
import repository.dto.{AccountsDTO, TransactionDTO}
import repository.mapping.{AccountsTable, TransactionTable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Handles database operations related to account and transaction management.
 *
 * @param datastore The Slick Database instance used for querying and updating data
 */
class OperationHandler(datastore: Database) extends Logging {
  private val transactionTable = TableQuery[TransactionTable]
  private val accountsTable = TableQuery[AccountsTable]

  /**
   * Fetches the account details for a given account ID.
   *
   * @param id The ID of the account to fetch
   * @return   An `Option[AccountsDTO]` with account details if found, otherwise None or empty
   */
  def fetchAccount(id: Long): Option[AccountsDTO] = {
    val passwordQuery = accountsTable.filter(acc => acc.id === id).result
    try {
      Await.result(datastore.run(passwordQuery), Duration.Inf).headOption
    } catch {
      case err: Throwable =>
        logger.error(s"database error: exception while reading transaction. ${err.getMessage}")
        Some(AccountsDTO.empty)
    }
  }

  /**
   * Inserts a new transaction record into the transaction log table.
   *
   * @param transaction The transaction data to be inserted
   * @return            The generated transaction ID if successful, otherwise 0
   */
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

  /**
   * Updates the balance and version of an account.
   *
   * @param accID   The account ID to update
   * @param balance The new balance to set
   * @param version The new version number for optimistic locking
   * @return        The number of rows affected (should be 1 if successful, 0 otherwise)
   */
  def updateAccount(accID: Long, balance: BigDecimal, version: Int): Int = {
    logger.info("updating account balance", accID, balance, version)
    val updateAccount = accountsTable.filter(acc => acc.id === accID)
    try {
      Await.result(
        datastore.run(updateAccount.map(acc => (acc.balance, acc.version)).update((Some(balance), Some(version)))),
        Duration.Inf
      )
    } catch {
      case _: Throwable =>
        logger.error(s"database error: exception during updating balance")
        0
    }
  }
}
