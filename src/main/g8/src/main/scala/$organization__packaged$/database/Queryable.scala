package $organization$.database

import $organization$.config.AppConfig
import doobie.Read
import doobie.implicits._
import doobie.util.fragment.Fragment
import monix.eval.Task

trait Queryable[F[_]] {
  def query[O: Read](
                      sql: Fragment
                    ): F[Vector[O]]
}

class MySql(
             implicit appConfig: AppConfig
           ) extends Queryable[Task] {
  override def query[O: Read](
                               sql: Fragment
                             ): Task[Vector[O]] = {
    appConfig
      .mysqlConfig
      .databaseConfig
      .transactorResource
      .use(
        transactor =>
          sql.query[O].to[Vector].transact(transactor)
      )
  }
}

class Vertica(
               implicit appConfig: AppConfig
             ) extends Queryable[Task] {
  override def query[O: Read](
                               sql: Fragment
                             ): Task[Vector[O]] = {
    appConfig
      .verticaConfig
      .databaseConfig
      .transactorResource
      .use(
        transactor =>
          sql.query[O].to[Vector].transact(transactor)
      )
  }
}