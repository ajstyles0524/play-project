package models

import play.api.libs.json.Json
case class Player(id: Long =1, name: String, country: String, role: Seq[String])

object Player {
  implicit val format = Json.format[Player]
}