package models

import play.api.libs.json.{Json, OFormat}

case class IPLTeam(id: Long, name: String, players: Seq[String])

object IPLTeam {
  implicit val teamFormat: OFormat[IPLTeam] = Json.format[IPLTeam]
}