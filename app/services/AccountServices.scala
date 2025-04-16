package services

import models.RequestModel
import models.dto.{AccountsDTO, TransactionDTO}
import play.api.mvc.Result
import repository.DBConnection
import play.api.mvc.Results._
import utils.Logging

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * This service handles account-related operations such as deposit, withdrawal, and balance checking.
 * It uses a synchronous style inside a `Future` block to interact with the database and return Play `Result`s.
 */
class AccountServices extends Logging {

  /**
   * Handles withdrawal from an account.
   *
   * @param request The request model containing account ID, password, and withdrawal amount
   * @return A `Future[Result]` indicating success or failure of the transaction
   */
  def withdraw(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get

        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")

          if (request.amount <= accountDetails.balance) {
            val newBalance = accountDetails.balance.getOrElse(BigDecimal(0)) - request.amount.getOrElse(BigDecimal(0))

            val transactionDetails = TransactionDTO(
              id = 0,
              transactionId = Some(UUID.randomUUID()),
              accountId = accountDetails.id,
              operation = "WITHDRAW",
              amount = request.amount.get,
              transactionTime = LocalDateTime.now(),
              balanceAfter = newBalance
            )

            val updateAccountBalance = DBConnection.handleDB.updateAccount(
              accountDetails.id,
              newBalance,
              accountDetails.version.getOrElse(0) + 1
            )
            val updateTransaction = DBConnection.handleDB.updateTransaction(transactionDetails)

            if (updateAccountBalance > 0 && updateTransaction > 0) {
              Ok(s"Rs.${request.amount.get} withdrawn successfully")
            } else {
              BadRequest("Error processing withdrawal -----retry")
            }
          } else {
            BadRequest("Insufficient balance")
          }
        } else {
          BadRequest("Invalid password")
        }
      } catch {
        case _: Throwable =>
          logger.error(s"Database error: exception during updating transaction for account ${request.account_id}")
          InternalServerError(s"Database error: exception during updating transaction for account ${request.account_id}")
      }
    }
  }

  /**
   * Handles deposit into an account.
   *
   * @param request The request model containing account ID, password, and deposit amount
   * @return A `Future[Result]` indicating success or failure of the transaction
   */
  def deposit(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get

        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")

          val newBalance = accountDetails.balance.getOrElse(BigDecimal(0)) + request.amount.getOrElse(BigDecimal(0))

          val transactionDetails = TransactionDTO(
            id = 0,
            transactionId = Some(UUID.randomUUID()),
            accountId = accountDetails.id,
            operation = "DEPOSIT",
            amount = request.amount.get,
            transactionTime = LocalDateTime.now(),
            balanceAfter = newBalance
          )

          val updateAccountBalance = DBConnection.handleDB.updateAccount(
            accountDetails.id,
            newBalance,
            accountDetails.version.getOrElse(0) + 1
          )
          val updateTransaction = DBConnection.handleDB.updateTransaction(transactionDetails)

          if (updateAccountBalance > 0 && updateTransaction > 0) {
            Ok(s"Rs.${request.amount.get} deposited successfully")
          } else {
            BadRequest("Error processing deposit ---retry")
          }
        } else {
          BadRequest("Invalid password")
        }
      } catch {
        case _: Throwable =>
          logger.error(s"Database error: exception during updating transaction for account ${request.account_id}")
          InternalServerError(s"Database error: exception during updating transaction for account ${request.account_id}")
      }
    }
  }

  /**
   * Handles checking the balance of an account.
   *
   * @param request The request model containing account ID and password
   * @return A `Future[Result]` containing the balance or error
   */
  def balanceCheck(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get

        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")
          Ok(s"Available balance is ${accountDetails.balance.getOrElse(0)}")
        } else {
          BadRequest("Invalid password")
        }
      } catch {
        case _: Throwable =>
          logger.error(s"Database error: exception during fetching balance for ${request.account_id}")
          InternalServerError(s"Database error: exception during fetching balance for ${request.account_id}")
      }
    }
  }
}
