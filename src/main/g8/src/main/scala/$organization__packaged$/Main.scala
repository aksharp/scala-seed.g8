package $organization$

import cats.effect.{Clock, ExitCode}
import $organization$.config._
import $organization$.feature.flags._
import $organization$.feature.flags.setup._
import $organization$.services._
import $organization$.http.Http4sRouter
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.feature.flags._
import com.tremorvideo.lib.metrics.LoadMetrics
import $organization$.processors.GreetRequestProcessor
import $organization$.util.ServiceObserverImpl
import $organization$.validators.GreetRequestValidator
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler.Implicits.global
import com.tremorvideo.lib.api.example.server

object Main extends TaskApp with LazyLogging {

  val supportedFeatureFlags: List[FeatureFlagsParent[Task]] = List(
    GreetFeatureFlags // TODO: !!!replace-me!!! with real feature flags
  )

  override def run(args: List[String]): Task[ExitCode] = {

    // load config or exit with error
    implicit val appConfig: AppConfig = AppConfigLoader.loadOrExitWithErrorMessage(args.toArray)

    for {
      // todo: make implicit
      // datadog metrics
      metrics <- LoadMetrics[Task](appConfig.metrics)

      // abstracts date and time so app can be easily tested
      clock = Clock.create[Task]

      // generates correlation ids for distributed tracing
      correlationIdGeneratorService = new CorrelationIdGeneratorService[Task]()
      // helper to new up ObservableAndTraceable or transform from existing ObservableAndTraceable
      // ObservableAndTraceable is the data structure that includes correlation ids and timestamp
      // this is used under the hood to observe, all that is needed is to have implicit reference to this service
      implicit0(t: ObservableAndTraceableService[Task]) = {
        new ObservableAndTraceableService[Task](
          serviceName = appConfig.appName,
          clock = clock,
          correlationIdGeneratorService = correlationIdGeneratorService
        )
      }

      // feature flags
      featureFlagsPoller = new FeatureFlagsPollerMonixImpl

      // observable service
      serviceObserver = new ServiceObserverImpl(
        clock = clock,
        supportedFeatureFlags = supportedFeatureFlags
      )

      // application code: services, caches, etc.
      greetRequestProcessor = new GreetRequestProcessor[Task]

      // validators
      greetRequestValidator = new GreetRequestValidator[Task]

      // grpc services / routes
      greeterGrpcService = new GreeterService(
        greetRequestValidator = greetRequestValidator,
        greetRequestProcessor = greetRequestProcessor
      )

      _ <- featureFlagsPoller.run(
        appConfig = appConfig,
        supportedFeatureFlags = supportedFeatureFlags
      )

      _ <- serviceObserver.run()

      // grpc server
      _ = server.run(
        greeter = greeterGrpcService
      )

      // http server
      http4sRouter = new Http4sRouter
      exitCode <- http4sRouter.run()
    } yield {
      exitCode
    }
  }
}
