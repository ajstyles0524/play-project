package models

import play.api.libs.json.{Json, OFormat}

case class Match(id: Long,team_id1: Long, team_id2: Long,tournamentId: Long)

object Match {
  implicit val matchFormat: OFormat[Match] = Json.format[Match]
}


