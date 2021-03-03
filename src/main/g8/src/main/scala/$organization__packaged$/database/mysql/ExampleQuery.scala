package $organization$.database.mysql

import $organization$.config.AppConfig
import $organization$.database.MySql
import $organization$.domain._
import doobie.implicits._
import monix.eval.Task

class ExampleQuery(
                    mysql: MySql
                  )
                  (
                    implicit appConfig: AppConfig
                  ) {

  private case class DAO(
                          id: Long,
                          name: String
                        )

  def query(
             id: Long
           ): Task[Vector[Spend]] = {

    for {
      daos <- ssdbg.query[DAO](
        sql =
          sql"""
            SELECT some_id           as id,
                   some_name         as name
            from some_table
            where some_other_id = $id
            ;
          """
      )
    } yield {
      daos.map(
        dao =>
          MyDomainObject(
            id = dao.id,
            name = dao.name,
            description = "example query"
          )
      )
    }
  }

}