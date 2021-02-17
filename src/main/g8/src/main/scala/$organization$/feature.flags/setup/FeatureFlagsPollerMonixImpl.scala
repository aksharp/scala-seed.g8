package $organization$.feature.flags.setup

import $organization$.config.AppConfig
import $organization$.util.ThreadPools
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags.{ConsulFeatureFlagsPoller, MonixConsulFeatureFlagsPoller}
import com.tremorvideo.lib.kafka.producer.{MonixKafkaProducer, TremorKafkaProducer, TremorKafkaProducerConfig}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler.global

import java.util.concurrent.BlockingQueue

class FeatureFlagsPollerMonixImpl extends FeatureFlagsPoller[Task] with LazyLogging {
  override def createPoller(
                             appConfig: AppConfig,
                             producer: TremorKafkaProducer[Task, ObservableAndTraceable, FeatureFlagsJson],
                             observableQueue: BlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)]
                           ): Task[ConsulFeatureFlagsPoller[Task]] =
    Task {
      new MonixConsulFeatureFlagsPoller(
        appName = appConfig.appName,
        consulHostname = appConfig.consulDynamicConfig.consulHostname,
        consulPort = appConfig.consulDynamicConfig.consulPort,
        observableProducer = producer,
        observableQueue = observableQueue,
        observableProducerScheduler = ThreadPools.featureFlagsThreadPool
      )
    }

  override def createProducer(
                               appConfig: AppConfig
                             ): Task[TremorKafkaProducer[Task, ObservableAndTraceable, FeatureFlagsJson]] =
    Task {
      import com.tremorvideo.lib.api.FeatureFlagsJsonSerde._
      import com.tremorvideo.lib.api.ObservableAndTraceableSerde._

      new MonixKafkaProducer[ObservableAndTraceable, FeatureFlagsJson](
        config = TremorKafkaProducerConfig(
          clientId = s"${appConfig.appName}-feature-flag-client",
          topic = s"observable-feature-flags",
          bootstrapServers = appConfig.observableDynamicConfigProducer.bootstrapServers
        ),
        executeOn = global
      )
    }
}
