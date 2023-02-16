package controllers
import models.Player
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class PlayerControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "PlayerController" should {

    "add player and search to verify" in {
      val testPlayer1 = Player(1,"testPlayer1","testCountry", Seq("testRoles"))
      val addPlayerRequest = FakeRequest(POST, "/api/player").withBody(Json.toJson(testPlayer1))
      val addPlayerResPonse = route(app, addPlayerRequest).get
      status(addPlayerResPonse) mustBe OK

      val name = (contentAsJson(addPlayerResPonse) \ "name").as[String]

      val deletePlayerRequest = FakeRequest(DELETE, s"/api/player/$name")
      val deletePlayerResponse = route(app, deletePlayerRequest).get
      status(deletePlayerResponse) mustBe OK
    }

    "get by name and search verify" in {
      val testPlayer2 = Player(2,"testPlayer11","testCountry", Seq("testRoles"))
      val addPlayerRequest = FakeRequest(POST, "/api/player").withBody(Json.toJson(testPlayer2))
      val addPlayerResPonse = route(app, addPlayerRequest).get
      status(addPlayerResPonse) mustBe OK
      val name = (contentAsJson(addPlayerResPonse) \ "name").as[String]

      val request = FakeRequest(GET, s"/api/player/byname/$name")
      val playerRes = route(app, request).get

      status(playerRes) mustBe OK
      contentAsJson(playerRes).as[Seq[Player]].head.name mustBe name

      val deletePlayerRequest = FakeRequest(DELETE, s"/api/player/$name")
      val deletePlayerResponse = route(app, deletePlayerRequest).get
      status(deletePlayerResponse) mustBe OK
    }
  }
}

