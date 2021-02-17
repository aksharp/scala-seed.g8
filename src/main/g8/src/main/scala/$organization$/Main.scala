package $organization$

import cats.effect.Clock
import $organization$.config.{AppConfig, AppConfigLoader}
import $organization$.feature.flags.{BidResponseFeatureFlags, BlackListFeatureFlags, PlacementFeatureFlags, PriceFeatureFlags, UserDataFeatureFlags, UserTargetFeatureFlags}
import $organization$.feature.flags.setup.{FeatureFlagsPoller, FeatureFlagsPollerMonixImpl}
import $organization$.grpc.{BidderController, GrpcServer}
import $organization$.services._
import $organization$.util.WarmUp
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags.FeatureFlagsParent
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue}

object Main extends LazyLogging {

  // how much to keep on the queue before sending to observable-persister
  val observationQueueCapacity = 10000
  val observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)] =
    new ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)](
      observationQueueCapacity
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
    val featureFlagsPoller: FeatureFlagsPoller[Task] = new FeatureFlagsPollerMonixImpl
    val supportedFeatureFlags: List[FeatureFlagsParent[Task]] = List(
      ExampleFeatureFlags // TODO: !!!replace-me!!! with real feature flags
    )

    // observable service
    val serviceObserver = new ServiceObserverImpl(
      appConfig = appConfig,
      clock = clock,
      supportedFeatureFlags = supportedFeatureFlags
    )

    // application code: services, caches, etc.
    // !!!replace-me!!! TODO:// initialize services here


    // grpc server
    val runGrpcServer = Task {
      // !!!replace-me!!! TODO:// copy code here from ExampleMain in target directory
    }

    // run
    (for {
      _ <- featureFlagsPoller.run(
        appConfig = appConfig,
        observableQueue = observableQueue,
        supportedFeatureFlags = supportedFeatureFlags
      )
      _ <- runGrpcServer
      _ <- serviceObserver.run()
    } yield {
      grpcServer.blockUntilShutdown()
    }).runSyncUnsafe()
  }
}
