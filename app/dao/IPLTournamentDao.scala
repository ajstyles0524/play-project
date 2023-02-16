package dao

import models.{IPLTeam, IPLTournament}

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class IPLTournamentDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  // (id: Long, name: String, place: String, teams: Seq[IPLTeam])

  class IPLTournamentTable(tag: Tag) extends Table[IPLTournament](tag, "tournament") {
    implicit val stringListMapper = MappedColumnType.base[Seq[String], String](
      list => list.mkString(","),
      string => string.split(',').toSeq
    )

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def place: Rep[String] = column[String]("place")

    def teams: Rep[Seq[String]] = column[Seq[String]]("teams")(stringListMapper)

    def * = (id, name, place, teams) <> ((IPLTournament.apply _).tupled, IPLTournament.unapply)
  }

  private val tournament = TableQuery[IPLTournamentTable]

  def insert(data: IPLTournament): Future[IPLTournament] = {
    db.run(DBIO.seq(tournament += data)) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { _ => data }
  }

  def filterQuery(id: Long): Query[IPLTournamentTable, IPLTournament, Seq] = tournament.filter(_.id === id)

  def filterByNameQuery(name: String): Query[IPLTournamentTable, IPLTournament, Seq] = tournament.filter(_.name === name)


  def findByName(name: String): Future[Seq[IPLTournament]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }


  def list(): Future[Seq[IPLTournament]] = db.run {
    tournament.result
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }


}
