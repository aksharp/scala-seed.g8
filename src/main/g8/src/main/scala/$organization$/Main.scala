package $organization$

import $organization$.config._
import $organization$.feature.flags._
import $organization$.feature.flags.setup._
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags._
import com.typesafe.scalalogging.LazyLogging
import $organization$.services._
import $organization$.grpc._
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import cats.effect.Clock

object Main extends LazyLogging {

  // how much to keep on the queue before sending to observable-persister
  val observationQueueCapacity = 10000
  val observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)] =
    new ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)](
      observationQueueCapacity
    )

  val supportedFeatureFlags: List[FeatureFlagsParent[Task]] = List(
    ExampleFeatureFlags // TODO: !!!replace-me!!! with real feature flags
  )

  def main(args: Array[String]): Unit = {

    // config
    implicit val appConfig: AppConfig = AppConfigLoader.loadOrExitWithErrorMessage(args)

    // generates correlation ids for distributed tracing
    val correlationIdGeneratorService = new CorrelationIdGeneratorService[Task]()
    // abstracts date and time so app can be easily tested
    val clock = Clock[Task]
    // helper to new up ObservableAndTraceable or transform from existing ObservableAndTraceable
    // ObservableAndTraceable is the data structure that includes correlation ids and timestamp
    // this is used under the hood to observe, all that is needed is to have implicit reference to this service
    implicit val observableAndTraceableService: ObservableAndTraceableService[Task] = {
      new ObservableAndTraceableService[Task](
        serviceName = appConfig.appName,
        clock = clock,
        correlationIdGeneratorService = correlationIdGeneratorService
      )
    }

    // feature flags
    val featureFlagsPoller = new FeatureFlagsPollerMonixImpl

    // observable service
    val serviceObserver = new ServiceObserverImpl(
      appConfig = appConfig,
      clock = clock,
      supportedFeatureFlags = supportedFeatureFlags
    )

    // application code: services, caches, etc.
    val greeterService = new GreeterServiceImpl

    // grpc services / routes
    val greeterGrpcService = new GreeterGrpcService(
      greeterService = greeterService
    )

    // run
    (for {
      _ <- featureFlagsPoller.run(
        appConfig = appConfig,
        observableQueue = observableQueue,
        supportedFeatureFlags = supportedFeatureFlags
      )
      _ <- serviceObserver.run()
    } yield {
      // grpc server
      server.run(
        greeter = greeterGrpcService
      )
    }).runSyncUnsafe()
  }
}
