package $organization$.util

import $organization$._
import cats.effect.Clock
import com.tremorvideo.api.observable.serde.ObservableServiceInstanceKeySerde._
import com.tremorvideo.api.observable.serde.ObservableServiceInstanceValueSerde._
import com.tremorvideo.api.observable.ObservableServiceInstanceStartup.ServiceType
import com.tremorvideo.api.observable._
import com.tremorvideo.api.feature.flags.FeatureFlagsJson
import com.tremorvideo.api.services.ObservableAndTraceableService
import com.tremorvideo.lib.kafka.producer.MonixKafkaProducer
import $organization$.config.AppConfig
import com.tremorvideo.lib.feature.flags.FeatureFlagsParent
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import monix.eval.Task
import monix.execution.Scheduler
import com.tremorvideo.api.example._
import java.io.File
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import scala.collection.immutable.ArraySeq
import scala.concurrent.duration.FiniteDuration

trait ServiceObserver[F[_]] extends LogInitialization {
  def run(): F[Unit]
}

class ServiceObserverImpl(
                           clock: Clock[Task],
                           supportedFeatureFlags: List[FeatureFlagsParent[Task]]
                         )
                         (
                           implicit observableAndTraceableService: ObservableAndTraceableService[Task],
                           appConfig: AppConfig
                         ) extends ServiceObserver[Task] {

  val observableServiceInstanceKeyProducer = new MonixKafkaProducer[ObservableServiceInstanceKey, ObservableServiceInstanceValue](
    config = appConfig.observableStaticConfigProducer,
    executeOn = Scheduler.fixedPool("observableServiceInstanceKeyProducer", 1)
  )

  implicit val finiteDurationEncoder: Encoder[FiniteDuration] =
    new Encoder[FiniteDuration] {
      final def apply(a: FiniteDuration): Json = Json.fromString(a.toString())
    }

  def run(): Task[Unit] = for {
    serviceInstanceCorrelationId <- observableAndTraceableService.serviceInstanceCorrelationId
    timestamp <- clock.realTime(TimeUnit.MILLISECONDS)
    _ <- observableServiceInstanceKeyProducer.send(
      key = ObservableServiceInstanceKey(
        serviceInstanceCorrelationId = serviceInstanceCorrelationId,
        timestamp = timestamp
      ),
      value = ObservableServiceInstanceStartup(
        name = appConfig.appName,
        version = Option(getClass.getPackage.getImplementationVersion).getOrElse("missing version"),
        dataCenter = appConfig.dataCenter,
        hostname = InetAddress.getLocalHost.getHostName,
        ip = InetAddress.getLocalHost.getHostAddress,
        config = appConfig.asJson.noSpaces,
        startTimestamp = timestamp,
        supportedApis = Seq(
          SupportedKafkaProducerApi(keyType = ObservableServiceInstanceKey.getClass.getName, valueType = ObservableServiceInstanceValue.getClass.getName),
          SupportedKafkaProducerApi(keyType = "ObservableAndTraceable", valueType = FeatureFlagsJson.getClass.getName),
          SupportedServerGrpcApi(
            serviceName = GreeterGrpc.METHOD_GREET.getServiceName,
            methodName = GreeterGrpc.METHOD_GREET.getBareMethodName,
            requestType = GreetRequest.getClass.getName,
            responseType = GreetResponse.getClass.getName,
            serverPort = 8080
          ),
          //          SupportedServerRestApi(uri = "_health", method = "GET")
        ),
        supportedFeatureFlags = supportedFeatureFlags.map {
          ff =>
            SupportedFeatureFlag(
              `type` = ff.featureName,
              consulPath = appConfig.url(ff.featureName)
            )
        },
        cpus = Runtime.getRuntime.availableProcessors,
        memory = Option(
          SystemMemory(
            maxMemory = Runtime.getRuntime.maxMemory,
            totalMemory = Runtime.getRuntime.totalMemory,
            freeMemory = Runtime.getRuntime.freeMemory
          )
        ),
        fileSystems = ArraySeq.unsafeWrapArray(File.listRoots()).map(
          root =>
            FileSystem(
              path = root.getAbsolutePath,
              totalSpace = root.getTotalSpace,
              freeSpace = root.getFreeSpace,
              usableSpace = root.getUsableSpace
            )
        ),
        serviceType = ServiceType.PROD, //TODO: find out how to get that from deploy
        cluster = "n/a" //TODO: find out how to get that from deploy
      )
    )
  } yield {
    logger.info(s"service config: $"$"${appConfig.serviceInstanceUrl(serviceInstanceCorrelationId)}")
  }

}
