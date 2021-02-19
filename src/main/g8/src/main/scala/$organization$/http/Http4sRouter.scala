package $organization$.http

import cats.effect.{ConcurrentEffect, ExitCode, Resource}
import $organization$.config.AppConfig
import $organization$.http.AppHttp4sDsl._
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.impl.Root
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Router, Server}

class Http4sRouter(
                    implicit F: ConcurrentEffect[Task],
                    scheduler: Scheduler,
                    appConfig: AppConfig,
                    observableAndTraceableService: ObservableAndTraceableService[Task]
                  ) extends LazyLogging {

  val routes: HttpRoutes[Task] = {
    HttpRoutes.of[Task] {
      case GET -> Root / appConfig.http.healthEndPoint => {
        Ok("healthy")
      }

      case GET -> Root / "info" => {
        for {
          serviceInstanceCorrelationId <- observableAndTraceableService.serviceInstanceCorrelationId
          response <- Ok(
            Map(
              "service-config" -> appConfig.serviceInstanceUrl(serviceInstanceCorrelationId)
            )
          )
        } yield {
          response
        }
      }
    }
  }

  def run(): Task[ExitCode] =
    app.use(_ => Task.never.as(ExitCode.Success))

  val app: Resource[Task, Server[Task]] =
    for {
      server <- BlazeServerBuilder[Task](scheduler)
        .bindHttp(port = appConfig.http.port, host = appConfig.http.host)
        .withHttpApp(
          Router[Task](
            "/" -> routes
          ).orNotFound
        )
        .resource
    } yield server

}
