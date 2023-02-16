package dao
import models.IPLTeam

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class IPLTeamDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  class IPLTeamTable(tag: Tag) extends Table[IPLTeam](tag, "ipl_team") {

    implicit val stringListMapper = MappedColumnType.base[Seq[String], String](
      list => list.mkString(","),
      string => string.split(',').toSeq
    )

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def players = column[Seq[String]]("players")(stringListMapper)

    def *  = (id, name, players) <> ((IPLTeam.apply _).tupled, IPLTeam.unapply)

  }

  private val team = TableQuery[IPLTeamTable]

  def insert(data: IPLTeam): Future[IPLTeam] = {
    db.run(DBIO.seq(team += data)) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { _ => data }
  }

  def update(data: IPLTeam): Future[Int] = {
    db.run(team.filter(_.id === data.id).update(data))
  }

  def list(): Future[Seq[IPLTeam]] = db.run {
    team.result
  }

  def filterQuery(id: Long): Query[IPLTeamTable, IPLTeam, Seq] = team.filter(_.id === id)


  def findPlayersByName(name: String): Future[List[String]] = {
    db.run(filterByNameQuery(name).result).map(data => data.head.players.toList)
  }

  def filterByNameQuery(name: String): Query[IPLTeamTable, IPLTeam, Seq] = team.filter(_.name === name)

  def findByName(name: String): Future[Seq[IPLTeam]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }
}