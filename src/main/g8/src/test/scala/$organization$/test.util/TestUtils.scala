package $organization$.test.util

import java.util.concurrent.ArrayBlockingQueue
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable, ObservableAndTraceableBase}
import com.tremorvideo.lib.feature.flags.{Debug, DebugToConsole, DoNotObserveByDefault, Observe}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducerConfig
import $organization$.config.{AppConfig, ConsulDynamicConfig}
import org.joda.time.DateTime
import org.json4s.DefaultFormats
import org.scalacheck.{Arbitrary, Gen}
import $organization$.services._

import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

trait TestUtils {

  def anObservableQueue(
                         capacity: Int = 100
                       ): ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)] =
    new ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)](
      capacity
    )

  def anAppConfig(
                   consulDynamicConfig: ConsulDynamicConfig = ConsulDynamicConfig(
                     consulHostname = "consul.service.iad1.consul",
                     consulPort = 8500
                   ),
                   appName: String = "$name$",
                   dataCenter: String = "iad1",
                   debug: Debug = DebugToConsole,
                   observe: Observe = DoNotObserveByDefault,
                   grpcServerPort: Int = 8080,
                   observableStaticConfigProducer: TremorKafkaProducerConfig = TremorKafkaProducerConfig(
                     clientId = "$name$-service",
                     topic = "observable-service-instance",
                     bootstrapServers = List("localhost:6001")
                   ),
                   observableDynamicConfigProducer: TremorKafkaProducerConfig = TremorKafkaProducerConfig(
                     clientId = "$name$-service",
                     topic = "observable-feature-flags",
                     bootstrapServers = List("localhost:6001")
                   )
                 ): AppConfig = AppConfig(
    consulDynamicConfig = consulDynamicConfig,
    appName = appName,
    dataCenter = dataCenter,
    grpcServerPort = grpcServerPort,
    observableStaticConfigProducer = observableStaticConfigProducer,
    observableDynamicConfigProducer = observableDynamicConfigProducer,
    debug = debug,
    observe = observe
  )
  
}