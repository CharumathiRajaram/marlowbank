package controllers

import models.RequestModel

import javax.inject._
import play.api._
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc._
import services.AccountServices
import scala.concurrent.{ExecutionContext, Future}

/**
 * Controller to handle ATM operations such as deposit, withdraw, and balance check.
 *
 * @param controllerComponents Play's base controller components injected via Guice
 * @param execution            ExecutionContext for handling asynchronous operations
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit execution: ExecutionContext) extends BaseController with Logging {
  val accountService = new AccountServices()


  /**
   * Generic request handler that validates the JSON input as a `RequestModel`
   * and calls the respective account service method.
   *
   * @param actionName  Name of the action (used in logging and error responses)
   * @param serviceCall Function that processes the valid `RequestModel` and returns a `Result`
   * @param request     The HTTP request
   * @return A `Future[Result]` representing the HTTP response
   */
  def handleRequest(actionName: String)(serviceCall: RequestModel => Future[Result])
                   (implicit request: Request[AnyContent]): Future[Result] = {
    request.body.asJson match {
      case Some(json) =>
        json.validate[RequestModel] match {
          case JsSuccess(value, _) =>
            serviceCall(value)
          case _ =>
            logger.error(s"Missing  credentials for $actionName")
            Future.successful(BadRequest(Json.parse(s"""{ "Error": "Missing credentials to $actionName" }""")))
        }
      case None =>
        logger.error(s"No input provided for $actionName")
        Future.successful(BadRequest(Json.parse(s"""{ "Error": "Please provide credentials to $actionName" }""")))
    }
  }

  /**
   * Handles withdrawal requests.
   *
   * @return HTTP Action to withdraw money from the account
   */
  def withdraw(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("withdraw")(accountService.withdraw)
  }

  /**
   * Handles deposit requests.
   *
   * @return HTTP Action to deposit money into the account
   */
  def deposit(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("deposit")(accountService.deposit)
  }

  /**
   * Handles balance check requests.
   *
   * @return HTTP Action to check account balance
   */
  def balanceCheck(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("balance check")(accountService.balanceCheck)
  }
}
