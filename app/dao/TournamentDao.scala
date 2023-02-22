package dao

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.Tournament

class TournamentDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class TournamentTable(tag: Tag) extends Table[Tournament](tag, "Tournament") {
    def id = column[Long]("id", O.AutoInc)
    def name = column[String]("name", O.PrimaryKey)
    def * = (id, name) <> ((Tournament.apply _).tupled, Tournament.unapply)
  }

  val tournaments = TableQuery[TournamentTable]

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
