package models

import play.api.libs.json.{Json, OFormat}

case class Team(id: Long, name: String)

object Team {
  implicit val teamFormat: OFormat[Team] = Json.format[Team]
}