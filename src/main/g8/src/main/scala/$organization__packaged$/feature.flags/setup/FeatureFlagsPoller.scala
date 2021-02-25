package $organization$.feature.flags.setup

import $organization$.config.AppConfig
import cats.Monad
import cats.implicits._
import com.tremorvideo.lib.api.feature.flags.FeatureFlagsJson
import com.tremorvideo.lib.api.observable.ObservableAndTraceable
import com.tremorvideo.lib.feature.flags.{ConsulFeatureFlagsPoller, FeatureFlagsParent}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducer
import com.typesafe.scalalogging.LazyLogging

trait FeatureFlagsPoller[F[_]] extends LazyLogging {
  def run(
           appConfig: AppConfig,
           supportedFeatureFlags: List[FeatureFlagsParent[F]]
         )
         (implicit M: Monad[F]): F[ConsulFeatureFlagsPoller[F]] = {
    for {
      producer <- createProducer(
        appConfig = appConfig
      )
      poller <- createPoller(
        appConfig = appConfig,
        producer = producer
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
                    producer: TremorKafkaProducer[F, ObservableAndTraceable, FeatureFlagsJson]
                  ): F[ConsulFeatureFlagsPoller[F]]

  def createProducer(
                      appConfig: AppConfig
                    ): F[TremorKafkaProducer[F, ObservableAndTraceable, FeatureFlagsJson]]
}


