package controllers

import models.{Match, Tournament}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{JsObject, Json}
import play.api.test._
import play.api.test.Helpers._


class TournamentControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "TournamentController" should {

    "add a tournament" in {
      val testTournament1 = Tournament(19, "W-IPL")
      val addTournamentRequest = FakeRequest(POST, "/tournament/add").withBody(Json.toJson(testTournament1))
      val addTournamentResponse = route(app, addTournamentRequest).get
      status(addTournamentResponse) mustBe OK
      contentAsJson(addTournamentResponse).as[Tournament] mustEqual testTournament1
    }

    "return all tournaments" in {
      val testTournaments = Seq(Tournament(1, "T-20"), Tournament(2, "BIG BASH"))
      val listAllTournamentRequest = FakeRequest(GET, "/tournament/all").withBody(Json.toJson(testTournaments))
      val listAllTournamentResponse = route(app, listAllTournamentRequest).get
      status(listAllTournamentResponse) mustEqual OK
      contentAsJson(listAllTournamentResponse).as[Seq[Tournament]] mustEqual testTournaments
    }

    " delete a tournament" in {
      val testTournament1 = Tournament(1, "PSL")
      val addTournamentRequest = FakeRequest(POST, "/tournament/add").withBody(Json.toJson(testTournament1))
      val addTournamentResponse = route(app, addTournamentRequest).get
      val name = (contentAsJson(addTournamentResponse) \ "name").as[String]
      val deleteTournamentRequest = FakeRequest(DELETE, s"/tournament/delete/$name")
      val deleteTournamentResponse = route(app, deleteTournamentRequest).get
      status(deleteTournamentResponse) mustBe OK

      val getTournamentRequest = FakeRequest(GET, "/tournament/all")
      val getTournamentResponse = route(app, getTournamentRequest).get
      contentAsJson(getTournamentResponse).as[Seq[Tournament]] must not contain testTournament1
    }

    "return matches by tournament name" in {
      val testMatches = Seq(
        Match(21, "CSK", "MI", "IPL"),
        Match(22, "KKR", "MI", "IPL"),
        Match(23, "INDIA", "SRI LANKA", "ASIA-CUP")
      )
      val tournamentName = "IPL"
      val matchesByTournamentIdRequest = FakeRequest(GET, s"/tournament/matches/$tournamentName")
      val matchesByTournamentIdResponse = route(app, matchesByTournamentIdRequest).get
      status(matchesByTournamentIdResponse) mustBe OK
      contentAsJson(matchesByTournamentIdResponse).as[Seq[Match]] mustEqual testMatches
    }

  }
}


