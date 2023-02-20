package dao

//import models.{CricketPlayer, CricketTeam}
//
//import javax.inject.{Inject, Singleton}
//import play.api.db.slick.DatabaseConfigProvider
//import play.api.libs.json.Json
//import slick.jdbc.JdbcProfile
//import slick.lifted.ProvenShape
//
//import scala.concurrent.{ExecutionContext, Future}
//
//
//@Singleton
//class CricketTeamDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//
//  class CricketTeamTable(tag: Tag) extends Table[CricketTeam](tag, "cricket_team") {
//
//    implicit val listPlayerMapper = MappedColumnType.base[List[CricketPlayer], String](
//      players => Json.toJson(players).toString(),
//      string => Json.parse(string).as[List[CricketPlayer]])
//
//
//    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//    def name: Rep[String] = column[String]("name")
//
//    def players: Rep[List[CricketPlayer]] = column[List[CricketPlayer]]("players")
//
//    def * : ProvenShape[CricketTeam] = (id, name, players).shaped <> ((CricketTeam.apply _).tupled, CricketTeam.unapply)
//
//  }
//  val team = TableQuery[CricketTeamTable]
//
//    def insert(data: CricketTeam): Future[CricketTeam] = {
//      db.run(DBIO.seq(team += data)) recover {
//        case t: Throwable =>
//          println("ERROR IS " + t.getLocalizedMessage)
//          throw t
//      } map { _ => data }
//    }
//
//    def update(data: CricketTeam): Future[Int] = {
//      db.run(team.filter(_.id === data.id).update(data))
//    }
//
//    def list(): Future[Seq[CricketTeam]] = db.run {
//      team.result
//    }
//
//  def findById(id: Long): Future[Option[CricketTeam]] =
//    db.run(team.filter(_.id === id).result.headOption)
//
//    def filterQuery(id: Long): Query[CricketTeamTable, CricketTeam, Seq] = team.filter(_.id === id)
//
//    def filterByNameQuery(name: String): Query[CricketTeamTable, CricketTeam, Seq] = team.filter(_.name === name)
//
//    def findByName(name: String): Future[Seq[CricketTeam]] = {
//      db.run(filterByNameQuery(name).result).map {
//        dataFromDb =>
//          println("Data is >>>>" + dataFromDb)
//          dataFromDb
//      }
//    }
//
//    def delete(id: Long): Future[Int] = {
//      val pp = db.run(filterQuery(id).delete)
//      pp
//    }
//}


import models.CricketTeam

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CricketTeamDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                               (implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketTeamTable(tag: Tag) extends Table[CricketTeam](tag, "cricket_team") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((CricketTeam.apply _).tupled, CricketTeam.unapply)
  }

  val cricketTeams = TableQuery[CricketTeamTable]

  def create(team: CricketTeam): Future[CricketTeam] = db.run {
    (cricketTeams.map(_.name)
      returning cricketTeams.map(_.id)
      into ((name, id) => CricketTeam(id, name))
      ) += team.name
  }

  def filterQuery(id: Long): Query[CricketTeamTable,CricketTeam, Seq] = cricketTeams.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketTeamTable,CricketTeam, Seq] = cricketTeams.filter(_.name === name)

  def findByName(name: String): Future[Seq[CricketTeam]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def getById(id: Long): Future[Option[CricketTeam]] = db.run {
    cricketTeams.filter(_.id === id).result.headOption
  }

  def teamsById(teamId: Long): Future[Seq[CricketTeam]] = db.run {
    cricketTeams.filter(_.id === teamId).result
  }

  def list(): Future[Seq[CricketTeam]] = db.run {
    cricketTeams.result
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

}