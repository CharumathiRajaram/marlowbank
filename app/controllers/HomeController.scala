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
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit execution: ExecutionContext) extends BaseController with Logging {
  val accountService = new AccountServices()


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

  def withdraw(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("withdraw")(accountService.withdraw)
  }

  def deposit(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("deposit")(accountService.deposit)
  }

  def balanceCheck(): Action[AnyContent] = Action.async { implicit request =>
    handleRequest("balance check")(accountService.balanceCheck)
  }
}
