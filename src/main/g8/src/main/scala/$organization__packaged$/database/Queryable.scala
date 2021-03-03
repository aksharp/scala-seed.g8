package $organization$.database

import $organization$.config.AppConfig
import doobie.Read
import doobie.implicits._
import doobie.util.fragment.Fragment
import monix.eval.Task
import cats.effect.{Bracket, Resource}
import doobie.hikari.HikariTransactor

trait Queryable[F[_]] {

  val transactorResource: Resource[F, HikariTransactor[F]]

  def query[O: Read](
                      sql: Fragment
                    )
                    (
                      implicit B: Bracket[F, Throwable]
                    ): F[Vector[O]] = {
    transactorResource
      .use(
        transactor =>
          sql.query[O].to[Vector].transact(transactor)
      )
  }
}

class MySql(
             implicit appConfig: AppConfig
           ) extends Queryable[Task] {

  override val transactorResource: Resource[Task, HikariTransactor[Task]] =
    appConfig
      .mysqlConfig
      .databaseConfig
      .transactorResource
}

class Vertica(
               implicit appConfig: AppConfig
             ) extends Queryable[Task] {

  override val transactorResource: Resource[Task, HikariTransactor[Task]] =
    appConfig
      .verticaConfig
      .databaseConfig
      .transactorResource
}