package controllers

import models.{Player, Team, Tournament}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class TeamControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "TeamController" should {

    "add a team" in {
      val testTeam1 = Team(1,"KXIP")
      val addTeamRequest = FakeRequest(POST, "/tournament/team/add").withBody(Json.toJson(testTeam1))
      val addTeamResponse = route(app, addTeamRequest).get
      status(addTeamResponse) mustBe OK
      contentAsJson(addTeamResponse).as[Team] mustEqual testTeam1
    }

    "return all teams" in {
      val testTeams = Seq(Team(2,"MI"),Team(3,"KKR"))
      val listAllTeamRequest = FakeRequest(GET, "/tournament/team/all").withBody(Json.toJson(testTeams))
      val listAllTeamResponse = route(app,listAllTeamRequest).get
      status(listAllTeamResponse) mustEqual OK
      contentAsJson(listAllTeamResponse).as[Seq[Team]] mustEqual testTeams
    }

    " delete a team" in {
      val testTeam1 = Team(4,"DC")
      val addTeamRequest = FakeRequest(POST, "/tournament/team/add").withBody(Json.toJson(testTeam1))
      val addTeamResponse = route(app, addTeamRequest).get
      val name = (contentAsJson(addTeamResponse) \ "name").as[String]
      val deleteTeamRequest = FakeRequest(DELETE, s"/tournament/team/delete/$name")
      val deleteTeamResponse = route(app, deleteTeamRequest).get
      status(deleteTeamResponse) mustBe OK

      val getTeamRequest = FakeRequest(GET, "/tournament/team/all")
      val getTeamResponse = route(app, getTeamRequest).get
      contentAsJson(getTeamResponse).as[Seq[Tournament]] must not contain testTeam1
    }
  }
}
