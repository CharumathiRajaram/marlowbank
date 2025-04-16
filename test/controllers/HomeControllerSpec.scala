package controllers

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.Play.materializer
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._
import services.AccountServices

import scala.concurrent.Future

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with MockitoSugar {

  val mockAccountService: AccountServices = mock[AccountServices]

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(bind[AccountServices].toInstance(mockAccountService))
    .build()

  "Home controller" should {
    "return OK on balanceCheck" in {
      val requestJson = Json.obj("account_id" -> "1","password"->"securePassword123")
      when(mockAccountService.balanceCheck(any())).thenReturn(Future.successful(Results.Ok("Balance: 1000")))

      val request = FakeRequest(POST, "/balanceCheck").withJsonBody(requestJson)
      val controller = app.injector.instanceOf[HomeController]
      val result = call(controller.balanceCheck(), request)

      status(result) mustBe OK
      contentAsString(result) must include("Available balance is ")
    }
    "return OK on withdraw" in {
      val requestJson = Json.obj("account_id" -> "1", "amount" -> 100.0,"password"->"securePassword123")
      when(mockAccountService.withdraw(any())).thenReturn(Future.successful(Results.Ok("Withdraw successful")))

      val request = FakeRequest(POST, "/withdraw").withJsonBody(requestJson)
      val controller = app.injector.instanceOf[HomeController]
      val result = call(controller.withdraw(), request)

      status(result) mustBe OK
      contentAsString(result) must include("Rs.100 withdrawn successfully")
    }

    "return OK on deposit" in {
      val requestJson = Json.obj("account_id" -> "1", "amount" -> 100.0,"password"->"securePassword123")
      when(mockAccountService.deposit(any())).thenReturn(Future.successful(Results.Ok("Deposit successful")))

      val request = FakeRequest(POST, "/deposit").withJsonBody(requestJson)
      val controller = app.injector.instanceOf[HomeController]
      val result = call(controller.deposit(), request)

      status(result) mustBe OK
      contentAsString(result) must include("Rs.100 deposited successfully")
    }



    "return BadRequest when input is missing" in {
      val request = FakeRequest(POST, "/deposit")
      val controller = app.injector.instanceOf[HomeController]
      val result = call(controller.deposit(), request)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Please provide credentials")
    }
  }
}
