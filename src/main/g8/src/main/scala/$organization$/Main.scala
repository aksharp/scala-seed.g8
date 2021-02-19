package $organization$

import cats.effect.{Clock, ExitCode}
import $organization$.config._
import $organization$.feature.flags._
import $organization$.feature.flags.setup._
import $organization$.grpc._
import $organization$.http.Http4sRouter
import $organization$.services._
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags._
import com.tremorvideo.lib.metrics.LoadMetrics
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler.Implicits.global

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

object Main extends TaskApp with LazyLogging {

  // how much to keep on the queue before sending to observable-persister
  val observationQueueCapacity = 10000
  val observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)] =
    new ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)](
      observationQueueCapacity
    )

  val supportedFeatureFlags: List[FeatureFlagsParent[Task]] = List(
    ExampleFeatureFlags // TODO: !!!replace-me!!! with real feature flags
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
        appConfig = appConfig, //todo: consume implicitly
        clock = clock,
        supportedFeatureFlags = supportedFeatureFlags
      )

      // application code: services, caches, etc.
      greeterService = new GreeterServiceImpl

      // grpc services / routes
      greeterGrpcService = new GreeterGrpcService(
        greeterService = greeterService
      )

      _ <- featureFlagsPoller.run(
        appConfig = appConfig,
        observableQueue = observableQueue,
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
