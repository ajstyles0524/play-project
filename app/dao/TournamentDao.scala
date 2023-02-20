package dao


//import models.{Match, Tournament}
//
//import javax.inject.{Inject, Singleton}
//import play.api.db.slick.DatabaseConfigProvider
//import play.api.libs.json.Json
//import slick.jdbc.JdbcProfile
//
//import scala.concurrent.{ExecutionContext, Future}
//
//
//@Singleton
//class TournamentDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//  // (id: Long, name: String, matches: List[Match])
//
//  class TournamentTable(tag: Tag) extends Table[Tournament](tag, "tournament") {
//
//    implicit val listMatchMapper = MappedColumnType.base[List[Match], String](
//      players => Json.toJson(players).toString(),
//      string => Json.parse(string).as[List[Match]])
//
//    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//    def name: Rep[String] = column[String]("name")
//
//    def matches: Rep[List[Match]]  = column[List[Match]]("matches")
//
//    def * = (id, name, matches) <> ((Tournament.apply _).tupled, Tournament.unapply)
//  }
//
//  private val tournament = TableQuery[TournamentTable]
//
//  def insert(data: Tournament): Future[Tournament] = {
//    db.run(DBIO.seq(tournament += data)) recover {
//      case t: Throwable =>
//        println("ERROR IS " + t.getLocalizedMessage)
//        throw t
//    } map { _ => data }
//  }
//
//  def filterQuery(id: Long): Query[TournamentTable, Tournament, Seq] = tournament.filter(_.id === id)
//
//  def filterByNameQuery(name: String): Query[TournamentTable, Tournament, Seq] = tournament.filter(_.name === name)
//
//  def findByName(name: String): Future[Seq[Tournament]] = {
//    db.run(filterByNameQuery(name).result).map {
//      dataFromDb =>
//        println("Data is >>>>" + dataFromDb)
//        dataFromDb
//    }
//  }
//
//  def list(): Future[Seq[Tournament]] = db.run {
//    tournament.result
//  }
//
//  def delete(id: Long): Future[Int] = {
//    val pp = db.run(filterQuery(id).delete)
//    pp
//  }
//}


import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Tournament

class TournamentDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class TournamentTable(tag: Tag) extends Table[Tournament](tag, "tournament") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((Tournament.apply _).tupled, Tournament.unapply)
  }

  val tournaments = TableQuery[TournamentTable]
  //private val matches = TableQuery[MatchTable]

  def add(tournament: Tournament): Future[Tournament] = db.run {
    (tournaments.map(t => t.name)
      returning tournaments.map(_.id)
      into ((name, id) => tournament.copy(id = id, name = name))
      ) += tournament.name
  }

  def list(): Future[Seq[Tournament]] = db.run {
    tournaments.result
  }

  def getById(id: Long): Future[Option[Tournament]] = db.run {
    tournaments.filter(_.id === id).result.headOption
  }

  def update(id: Long, name: String): Future[Int] = db.run {
    tournaments.filter(_.id === id).map(_.name).update(name)
  }


  def filterQuery(id: Long): Query[TournamentTable, Tournament, Seq] = tournaments.filter(_.id === id)

  def filterByNameQuery(name: String): Query[TournamentTable, Tournament, Seq] = tournaments.filter(_.name === name)

  def findByName(name: String): Future[Seq[Tournament]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def delete(id: Long): Future[Int] = db.run {
    tournaments.filter(_.id === id).delete
  }
}
