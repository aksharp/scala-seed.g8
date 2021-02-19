package $organization$.test.util

import $organization$.config.{AppConfig, ConsulDynamicConfig, HttpConfig}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable}
import com.tremorvideo.lib.feature.flags.{Debug, DebugToConsole, DoNotObserveByDefault, Observe}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducerConfig
import com.tremorvideo.lib.metrics.{MetricsReporter, NoOp}

import java.util.concurrent.ArrayBlockingQueue

trait TestUtils {

  def anObservableQueue(
                         capacity: Int = 100
                       ): ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)] =
    new ArrayBlockingQueue[(ObservableAndTraceable, FeatureFlagsJson)](
      capacity
    )

  def anAppConfig(
                   http: HttpConfig = HttpConfig(
                     host = "0.0.0.0",
                     port = 8888,
                     healthEndPoint = "_health"
                   ),
                   metrics: MetricsReporter = NoOp,
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
    http = http,
    metrics = metrics,
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