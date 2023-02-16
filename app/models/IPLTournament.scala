package models

import play.api.libs.json.{Json, OFormat}


case class IPLTournament(id: Long, name: String, place: String, teams: Seq[String])

object IPLTournament {
  implicit val tournamentFormat: OFormat[IPLTournament] = Json.format[IPLTournament]
}