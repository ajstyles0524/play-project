package models

import play.api.libs.json.{Json, OFormat}

case class Student(id: Long, name: String, age: Int, section: Int, role: Seq[String])

object Student {
  implicit val format: OFormat[Student] = Json.format[Student]
}


