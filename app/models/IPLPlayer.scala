package models

import play.api.libs.json.{Json, OFormat}

case class IPLPlayer(id: Long, name: String, score: Int)

object IPLPlayer {
  implicit val playerFormat: OFormat[IPLPlayer] = Json.format[IPLPlayer]
}