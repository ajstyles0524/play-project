package models

import play.api.libs.json.{Json, OFormat}

case class Match(id: Long,team_name1: String, team_name2: String,tournament_name: String)

object Match {
  implicit val matchFormat: OFormat[Match] = Json.format[Match]
}


