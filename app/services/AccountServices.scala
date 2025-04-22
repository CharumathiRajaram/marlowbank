package services

import models.RequestModel
import models.dto.{AccountsDTO, TransactionDTO}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results._
import repository.DBConnection
import utils.Logging

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountServices extends Logging {

  /**
   * Authenticates account based on provided credentials and executes the given block.
   *
   * @param request Request containing account ID and password
   * @param block   Function to execute with authenticated account
   * @return        Future of Result
   */
  private def withAccount(request: RequestModel)(block: AccountsDTO => Future[Result]): Future[Result] = {
    Future {
      DBConnection.handleDB.fetchAccount(request.account_id.toLong)
    }.flatMap {
      case Some(account) if request.password == account.password.getOrElse("") =>
        logger.info("Password validation successful")
        block(account)

      case Some(_) =>
        logger.warn("Invalid password")
        Future.successful(BadRequest(Json.obj("error" -> "Invalid password")))

      case None =>
        logger.warn(s"Account not found: ${request.account_id}")
        Future.successful(NotFound(Json.obj("error" -> s"User ${request.account_id} Not Found")))
    }
  }

  /**
   * Handles transaction logic including updating balance and recording the transaction.
   *
   * @param account     Authenticated account
   * @param request     Incoming request
   * @param operation   Type of operation: WITHDRAW or DEPOSIT
   * @param newBalance  Balance after transaction
   * @return            Future of Result
   */
  private def handleTransaction(
                                 account: AccountsDTO,
                                 request: RequestModel,
                                 operation: String,
                                 newBalance: BigDecimal
                               ): Future[Result] = Future {
    val transactionDetails = TransactionDTO(
      id = 0,
      transactionId = Some(UUID.randomUUID()),
      accountId = account.id,
      operation = operation,
      amount = request.amount.get,
      transactionTime = LocalDateTime.now(),
      balanceAfter = newBalance
    )

    val updateAccountBalance = DBConnection.handleDB.updateAccount(
      account.id,
      newBalance,
      account.version.getOrElse(0) + 1
    )
    val updateTransaction = DBConnection.handleDB.updateTransaction(transactionDetails)

    if (updateAccountBalance > 0 && updateTransaction > 0) {
      Ok(Json.obj("success" -> s"Rs.${request.amount.get} ${operation.toLowerCase}ed successfully"))
    } else {
      InternalServerError(Json.obj("error" -> s"${operation.capitalize} failed, please retry"))
    }
  }

  /**
   * Processes a withdrawal request.
   *
   * @param request RequestModel containing withdrawal data
   * @return        Future of Result
   */
  def withdraw(request: RequestModel): Future[Result] = {
    logger.info("withdraw::method_entry")
    withAccount(request) { account =>
      if (request.amount.get <= account.balance.getOrElse(BigDecimal(0))) {
        val newBalance = account.balance.get - request.amount.get
        handleTransaction(account, request, "WITHDRAW", newBalance)
      } else {
        Future.successful(BadRequest(Json.obj("error" -> "Insufficient balance")))
      }
    }.recover {
      case ex =>
        logger.error(s"Exception during withdrawal for account ${request.account_id}: ${ex.getMessage}")
        InternalServerError(Json.obj("error" -> s"Unexpected error while processing withdrawal"))
    }
  }

  /**
   * Processes a deposit request.
   *
   * @param request RequestModel containing deposit data
   * @return        Future of Result
   */
  def deposit(request: RequestModel): Future[Result] = {
    logger.info("deposit::method_entry")
    withAccount(request) { account =>
      val newBalance = account.balance.getOrElse(BigDecimal(0)) + request.amount.get
      handleTransaction(account, request, "DEPOSIT", newBalance)
    }.recover {
      case ex =>
        logger.error(s"Exception during deposit for account ${request.account_id}: ${ex.getMessage}")
        InternalServerError(Json.obj("error" -> s"Unexpected error while processing deposit"))
    }
  }

  /**
   * Checks and returns the account balance.
   *
   * @param request RequestModel with account credentials
   * @return        Future of Result with balance info
   */
  def balanceCheck(request: RequestModel): Future[Result] = {
    logger.info("balanceCheck::method_entry")
    withAccount(request) { account =>
      Future.successful(Ok(Json.obj("success" -> s"Available balance is ${account.balance.getOrElse(0)}")))
    }.recover {
      case ex =>
        logger.error(s"Exception during balance check for account ${request.account_id}: ${ex.getMessage}")
        InternalServerError(Json.obj("error" -> s"Unexpected error while checking balance"))
    }
  }
}
