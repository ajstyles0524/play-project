package models

import play.api.libs.json.{Json, OFormat}

case class Player(id: Long, name: String, score: Int, team_name: String)
object Player {
  implicit val playerFormat: OFormat[Player] = Json.format[Player]
}