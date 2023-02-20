package models

import play.api.libs.json.{Json, OFormat}

case class CricketPlayer(id: Long, name: String, score: Int, teamId: Long)
object CricketPlayer {
  implicit val playerFormat: OFormat[CricketPlayer] = Json.format[CricketPlayer]
}