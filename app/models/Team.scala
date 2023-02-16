package models

import play.api.libs.json.Json
case class Team(id: Long =1, name: String ="ramdas", score: Int = 33)

object Team {
  implicit val format = Json.format[Team]
}