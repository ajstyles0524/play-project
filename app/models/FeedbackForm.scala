package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, OFormat}

case class FeedbackForm(id: Int, name: String, email: String, rating: Int, comments: String)

object FeedbackForm {
  val form: Form[FeedbackForm] = Form(
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "email" -> email,
      "rating" -> number(min = 1, max = 5),
      "comments" -> nonEmptyText
    )(FeedbackForm.apply)(FeedbackForm.unapply))
  implicit val formFormat: OFormat[FeedbackForm] = Json.format[FeedbackForm]
}