package controllers

import models.{Player, Tournament}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import org.specs2.matcher.Matchers.beSome
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class PlayerControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "PlayerController" should {

    "add a player" in {
      val testPlayer1 = Player(90,"VINAY", 100, "CSK")
      val addPlayerRequest = FakeRequest(POST, "/tournament/team/player/add").withBody(Json.toJson(testPlayer1))
      val addPlayerResponse = route(app, addPlayerRequest).get
      status(addPlayerResponse) mustBe OK
      contentAsJson(addPlayerResponse) mustEqual Json.toJson(testPlayer1)
    }

    "return all tournaments" in {
      val testPlayers = Seq(Player(2,"Aman", 0, "MI"),Player(3,"Rama", 46, "PUNE"))
      val listAllPlayerRequest = FakeRequest(GET, "/tournament/team/player/all").withBody(Json.toJson(testPlayers))
      val listAllPlayerResponse = route(app,listAllPlayerRequest).get
      status(listAllPlayerResponse) mustEqual OK
      contentAsJson(listAllPlayerResponse) mustEqual Json.toJson(testPlayers)
    }

    " delete a player" in {
      val testPlayer1 = Player(76, "RAHUL", 45, "KKR")
      val addPlayerRequest = FakeRequest(POST, "/tournament/team/player/add").withBody(Json.toJson(testPlayer1))
      val addPlayerResponse = route(app, addPlayerRequest).get
      val name = (contentAsJson(addPlayerResponse) \ "name").as[String]
      val deletePlayerRequest = FakeRequest(DELETE, s"/tournament/team/player/delete/$name")
      val deletePlayerResponse = route(app, deletePlayerRequest).get
      status(deletePlayerResponse) mustBe OK

      val getPlayerRequest = FakeRequest(GET, "/tournament/team/player/all")
      val getPlayerResponse = route(app, getPlayerRequest).get
      contentAsJson(getPlayerResponse).as[Seq[Player]] must not contain testPlayer1
    }

  }
}
