package $organization$.config

import cats.Monad
import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.typesafe.scalalogging.LazyLogging
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import monix.eval.Task

case class DatabaseConfig(
                           url: String,
                           driver: String,
                           user: String,
                           passwordEnvVar: String,
                           timeZone: String,
                           poolSize: Int
                         ) extends LazyLogging {

  val transactorResource: Resource[Task, HikariTransactor[Task]] = getTransactor[Task]

  private def getTransactor[F[_] : Async : ContextShift](
                                                  implicit M: Monad[F]
                                                ): Resource[F, HikariTransactor[F]] = {
  // Resource yielding a transactor configured with a bounded connect EC and an unbounded
  // transaction EC. Everything will be closed and shut down cleanly after use.
    for {
      awaitConnectionHere <- ExecutionContexts.fixedThreadPool[F](poolSize) // connect EC
      executeJdbcHere <- Blocker[F] // blocking EC
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = driver,
        url = url,
        user = user,
        pass = getPassword,
        connectEC = awaitConnectionHere,
        blocker = executeJdbcHere
      )
    } yield xa
  }

  def getPassword: String = {
    val pwd = scala.sys.env(passwordEnvVar)
    if (pwd.isEmpty)
      logger.error(s"$"$"$passwordEnvVar env variable is not set or password is empty")
    pwd
  }
}
