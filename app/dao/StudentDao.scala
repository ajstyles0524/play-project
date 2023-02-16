package dao

import models.Student

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StudentDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  // (id: Long, name: String, age: Int, section: Int, Role: Seq[String])

  class StudentTable(tag: Tag) extends Table[Student](tag, "students") {
    implicit val stringListMapper = MappedColumnType.base[Seq[String], String](
      list => list.mkString(","),
      string => string.split(',').toSeq
    )

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def age: Rep[Int] = column[Int]("age")

    def section: Rep[Int] = column[Int]("section")

    def role: Rep[Seq[String]] = column[Seq[String]]("role")(stringListMapper)

    def *  = (id, name, age, section, role) <> ((Student.apply _).tupled, Student.unapply)
  }

  private val student = TableQuery[StudentTable]


  def insert(data: Student): Future[Student] = {
    db.run(DBIO.seq(student += data)) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { _ => data}
  }

  def update(data: Student): Future[Int] = {
    db.run(student.filter(_.id === data.id).update(data))
  }

  def list(): Future[Seq[Student]] = db.run {
    student.result
  }

  def find(id: Long): Future[Student] = {
    val pp = db.run(student.filter(_.id === id).result.head)
    pp
  }

  def filterQuery(id: Long): Query[StudentTable, Student, Seq] = student.filter(_.id === id)

  def filterByNameQuery(name: String): Query[StudentTable, Student, Seq] = student.filter(_.name === name)

  def findByName(name: String): Future[Seq[Student]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def findById(id: Long): Future[Seq[Student]] = {
    db.run(filterQuery(id).result).map {
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

