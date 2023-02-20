package models


import play.api.libs.json.{Json, OFormat}

case class Tournament(id: Long, name: String)

object Tournament {
  implicit val tournamentFormat: OFormat[Tournament] = Json.format[Tournament]
}