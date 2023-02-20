package models

import play.api.libs.json.{Json, OFormat}

case class CricketTeam(id: Long, name: String)

object CricketTeam {
  implicit val teamFormat: OFormat[CricketTeam] = Json.format[CricketTeam]
}