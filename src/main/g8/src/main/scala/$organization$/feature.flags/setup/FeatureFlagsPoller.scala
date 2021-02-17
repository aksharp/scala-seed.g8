package $organization$.feature.flags.setup

import cats.Monad
import cats.implicits._
import $organization$.config.AppConfig
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags.{ConsulFeatureFlagsPoller, FeatureFlagsParent}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducer
import com.typesafe.scalalogging.LazyLogging

import java.util.concurrent.BlockingQueue


trait FeatureFlagsPoller[F[_]] extends LazyLogging {
  def run(
           appConfig: AppConfig,
           observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)],
           supportedFeatureFlags: List[FeatureFlagsParent[F]]
         )
         (implicit M: Monad[F]): F[ConsulFeatureFlagsPoller[F]] = {
    for {
      producer <- createProducer(
        appConfig = appConfig
      )
      poller <- createPoller(
        appConfig = appConfig,
        producer = producer,
        observableQueue = observableQueue
      )
    } yield {
      // feature flags for poller to poll from consul, and send to kafka for observation
      poller.registerAll(
        supportedFeatureFlags
      )

      // execute poller
      poller.run
      logger.info(s"FeatureFlagsPoller started")
      poller
    }
  }

  def createPoller(
                    appConfig: AppConfig,
                    producer: TremorKafkaProducer[F, ObservableAndTraceable, FeatureFlagsJson],
                    observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)]
                  ): F[ConsulFeatureFlagsPoller[F]]

  def createProducer(
                      appConfig: AppConfig
                    ): F[TremorKafkaProducer[F, ObservableAndTraceable, FeatureFlagsJson]]
}


