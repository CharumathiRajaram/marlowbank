package services


import models.RequestModel
import play.api.mvc.Result
import repository.DBConnection
import play.api.mvc.Results._
import repository.dto.{AccountsDTO, TransactionDTO}
import utils.Logging

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountServices extends Logging {

  def withdraw(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get
        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")
          println(request.amount, accountDetails.balance)
          if (request.amount <= accountDetails.balance) {
            println(request.amount, accountDetails.balance)
            println(accountDetails)
            val newBalance = accountDetails.balance.getOrElse(BigDecimal(0)) - request.amount.getOrElse(BigDecimal(0))
            val transactionDetails = TransactionDTO(id = 0, transactionId = Some(UUID.randomUUID()), accountId = accountDetails.id, operation = "WITHDRAW", amount = request.amount.get, transactionTime = LocalDateTime.now(), balanceAfter = newBalance
            )
            val updateAccountBalance = DBConnection.handleDB.updateAccount(accountDetails.id, newBalance, accountDetails.version.getOrElse(0) + 1)
            val updateTransaction = DBConnection.handleDB.updateTransaction(transactionDetails)
            if (updateAccountBalance > 0 && updateTransaction > 0) {
              Ok(s"Rs.${request.amount.get} withdrawn successfully")
            } else {
              BadRequest("Error processing withdrawal -----retry")
            }
          }
          else {
            // Insufficient balance
            BadRequest("Insufficient balance")
          }
        } else {
          BadRequest("Invalid password")
        }
      }
      catch {
        case _: Throwable => logger.error(s"Database error: exception during updating transaction for account ${request.account_id}")
          InternalServerError(s"Database error: exception during updating transaction for account ${request.account_id}")
      }

    }
  }

  def deposit(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get
        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")
          println(request.amount, accountDetails.balance)
          val newBalance = accountDetails.balance.getOrElse(BigDecimal(0)) + request.amount.getOrElse(BigDecimal(0))
          val transactionDetails = TransactionDTO(id = 0, transactionId = Some(UUID.randomUUID()), accountId = accountDetails.id, operation = "DEPOSIT", amount = request.amount.get, transactionTime = LocalDateTime.now(), balanceAfter = newBalance
          )
          val updateAccountBalance = DBConnection.handleDB.updateAccount(accountDetails.id, newBalance, accountDetails.version.getOrElse(0) + 1)
          val updateTransaction = DBConnection.handleDB.updateTransaction(transactionDetails)
          if (updateAccountBalance > 0 && updateTransaction > 0) {
            Ok(s"Rs.${request.amount.get} deposited successfully")
          } else {
            BadRequest("Error processing deposit ---retry")
          }
        }
        else {
          BadRequest("Invalid password")
        }
      }
      catch {
        case _: Throwable => logger.error(s"Database error: exception during updating transaction for account ${request.account_id}")
          InternalServerError(s"Database error: exception during updating transaction for account ${request.account_id}")
      }

    }
  }

  //
  def balanceCheck(request: RequestModel): Future[Result] = {
    Future {
      try {
        logger.info("get account details")
        val accountDetails: AccountsDTO = DBConnection.handleDB.fetchAccount(request.account_id.toInt).get
        logger.info("validate password")
        if (request.password.equals(accountDetails.password.getOrElse(""))) {
          logger.info("password validation successful")
          Ok(s"Available balance is ${accountDetails.balance.getOrElse(0)}")
        }
        else {
          BadRequest("Invalid password")
        }
      }
      catch {
        case _: Throwable => logger.error(s"Database error: exception during fetching balance for ${request.account_id}")
          InternalServerError(s"Database error: exception during exception during fetching balance for ${request.account_id}")
      }
    }
  }
}

