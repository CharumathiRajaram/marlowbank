package models

import play.api.libs.json.{Json, OFormat}

case class RequestModel(account_id: String, password: String, amount: Option[BigDecimal])

object RequestModel {
  implicit val dataFormat: OFormat[RequestModel] = Json.format[RequestModel]
}
