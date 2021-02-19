package $organization$.test.util

import cats.effect.Clock
import $organization$.config.{AppConfig, ConsulDynamicConfig, HttpConfig}
import $organization$.feature.flags.ExampleFeatureFlags
import com.tremorvideo.lib.api.fp.util.{CorrelationIdGeneratorService, ObservableAndTraceableService}
import com.tremorvideo.lib.api.{FeatureFlagsJson, ObservableAndTraceable, ObservableAndTraceableBase}
import com.tremorvideo.lib.feature.flags.{Debug, DebugToConsole, DoNotObserveByDefault, Observe}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducerConfig
import com.tremorvideo.lib.metrics.{MetricsReporter, NoOp}
import monix.eval.Task
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}

import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

trait TestUtils {

  val testAppName = "$name$"
  val testClockTime: DateTime = DateTime.now()
  val testCorrelationIdGeneratorService = new CorrelationIdGeneratorService[Task]

  def aString() = Gen.alphaNumStr.sample.get

  val testServiceInstanceCorrelationId: String = aString()
  val testApiCallCorrelationId: String = aString()

  def anObservableAndTraceable(
                                serviceInstanceCorrelationId: String = testServiceInstanceCorrelationId,
                                apiCallCorrelationId: String = testApiCallCorrelationId,
                                currentDateTime: DateTime = testClockTime
                              ): ObservableAndTraceable =
    new ObservableAndTraceableBase(
      serviceInstanceCorrelationId = serviceInstanceCorrelationId,
      apiCallCorrelationId = apiCallCorrelationId,
      apiCallTimestamp = currentDateTime.getMillis
    )

  val testClock = new Clock[Task] {
    override def realTime(unit: TimeUnit): Task[Long] = Task.pure {
      FiniteDuration(testClockTime.getMillis, TimeUnit.MILLISECONDS).toUnit(
        unit
      ).toLong
    }

    override def monotonic(unit: TimeUnit): Task[Long] =
      Task.pure {
        FiniteDuration(testClockTime.getMillis, TimeUnit.MILLISECONDS).toUnit(
          unit
        ).toLong
      }
  }

  def anObservableAndTraceableService(
                                       serviceName: String = testAppName,
                                       clock: Clock[Task] = testClock,
                                       correlationIdGeneratorService: CorrelationIdGeneratorService[Task] = testCorrelationIdGeneratorService
                                     ) = new ObservableAndTraceableService[Task](
    serviceName = serviceName,
    clock = clock,
    correlationIdGeneratorService = correlationIdGeneratorService
  )

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

  def anExampleFeatureFlags(): ExampleFeatureFlags = genExampleFeatureFlags.sample.get

  val genExampleFeatureFlags: Gen[ExampleFeatureFlags] =
    for {
      allow <- Gen.listOf(Gen.alphaNumStr)
      block <- Gen.listOf(Gen.alphaNumStr)
      enable <- Gen.oneOf(true, false)
    } yield {
      ExampleFeatureFlags(
        allow = allow,
        block = block,
        enable = enable
      )
    }

  implicit val arbExampleFeatureFlags: Arbitrary[ExampleFeatureFlags] =
    Arbitrary(
      genExampleFeatureFlags
    )

}