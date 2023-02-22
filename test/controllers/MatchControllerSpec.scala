package controllers

import models.{Match, Player, Team, Tournament}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class MatchControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "MatchController" should {

    "add a match" in {
      val testMatch1 = Match(10,"CSK", "KKR","IPL")
      val addMatchRequest = FakeRequest(POST, "/tournament/match/add").withBody(Json.toJson(testMatch1))
      val addMatchResponse = route(app, addMatchRequest).get
      status(addMatchResponse) mustBe OK
    }

    "return all matches" in {
      val testMatches = Seq(Match(11, "INDIA","SRI LANKA","ASIA-CUP"), Match(12,"SRI LANKA", "PAKISTAN","ASIA-CUP"))
      val listAllMatchRequest = FakeRequest(GET, "/tournament/match/all").withBody(Json.toJson(testMatches))
      val listAllMatchResponse = route(app,listAllMatchRequest).get
      status(listAllMatchResponse) mustEqual OK
    }

    " delete a match" in {
      val testMatch1 = Match(13, "MI", "RCB","IPL")
      val addMatchRequest = FakeRequest(POST, "/tournament/match/add").withBody(Json.toJson(testMatch1))
      val addMatchResponse = route(app, addMatchRequest).get
      val id = (contentAsJson(addMatchResponse) \ "id").as[Long]
      val deleteMatchRequest = FakeRequest(DELETE, s"/tournament/match/delete/$id")
      val deleteMatchResponse = route(app, deleteMatchRequest).get
      status(deleteMatchResponse) mustBe OK
    }
  }
}
