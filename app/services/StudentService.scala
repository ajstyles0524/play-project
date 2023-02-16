package services

import com.google.inject.Singleton
import dao.StudentDao

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class StudentService @Inject() (studentDao: StudentDao) {
  def findAndDelete(name: String): Future[Int] = {
    studentDao.findByName(name).flatMap { students =>
      val foundStudent = students.find(_.name == name)
      foundStudent match {
        case Some(p) => studentDao.delete(p.id)
        case None => throw new Exception(s"student not found by name ${name}")
      }
    }
  }
}
