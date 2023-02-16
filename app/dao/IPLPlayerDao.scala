package dao

import models.IPLPlayer

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class IPLPlayerDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class IPLPlayerTable(tag: Tag) extends Table[IPLPlayer](tag, "ipl_player") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def score = column[Int]("score")

    def * = (id, name, score) <> ((IPLPlayer.apply _).tupled, IPLPlayer.unapply)
  }

  private val player = TableQuery[IPLPlayerTable]

  def insert(data: IPLPlayer): Future[IPLPlayer] = {
    db.run(DBIO.seq(player += data)) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { _ => data }
  }

  def update(data: IPLPlayer): Future[Int] = {
    db.run(player.filter(_.id === data.id).update(data))
  }

  def list(): Future[Seq[IPLPlayer]] = db.run {
    player.result
  }

  def filterQuery(id: Long): Query[IPLPlayerTable, IPLPlayer, Seq] = player.filter(_.id === id)

  def findScoreByName(name: String): Future[Int] = {
    db.run(filterByNameQuery(name).result).map(data => data.head.score)
  }

  def filterByNameQuery(name: String): Query[IPLPlayerTable, IPLPlayer, Seq] = player.filter(_.name === name)

  def findByName(name: String): Future[Seq[IPLPlayer]] = {
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