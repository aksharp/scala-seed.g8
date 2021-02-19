package $organization$.properties

import $organization$.config.AppConfig
import $organization$.feature.flags.ExampleFeatureFlags
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import $organization$.services.GreeterServiceImpl
import $organization$.test.util.TestUtils
import $organization$.{GreetRequest, GreetResponse, NotWelcomeResponse, OutOfServiceResponse, WelcomeResponse}
import monix.eval.Task
import monix.execution.Scheduler
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Properties}

import scala.concurrent.duration.Duration

object GreeterServiceProps extends Properties("GreeterServiceProps") with TestUtils {

  import $organization$.mocks._  // code generated mocks, generators and arbitraries

  // required implicits - can override with values as need be
  implicit val observableAndTraceableService: ObservableAndTraceableService[Task] = anObservableAndTraceableService()
  implicit val observableAndTraceable: ObservableAndTraceable = anObservableAndTraceable()
  implicit val appConfig: AppConfig = anAppConfig()
  implicit val scheduler: Scheduler = Scheduler.global

  val greeterService = new GreeterServiceImpl

  property(
    """
      INTENT: [GenerateGreetResponse]
      FEATURE FLAGS: [ExampleFeatureFlags]
      INPUT: [GreetRequest]
      OUTPUT: [GreetResponse]
        EITHER [WelcomeResponse] // happy path type
        OR [NotWelcomeResponse]
        OR [OutOfServiceResponse]
      """.stripMargin
  ) =
    forAll {
      (
        exampleFeatureFlags: ExampleFeatureFlags,
        input: GreetRequest
      ) => {
        ExampleFeatureFlags.set(exampleFeatureFlags)
        val greetResponse: GreetResponse =
          greeterService
            .process(
              greetRequest = input
            )
            .runSyncUnsafe(Duration.Inf)

        greetResponse match {
          case WelcomeResponse(_) => exampleFeatureFlags.enable && !exampleFeatureFlags.block.contains(input.name)
          case NotWelcomeResponse(_) => exampleFeatureFlags.enable && exampleFeatureFlags.block.contains(input.name)
          case OutOfServiceResponse(_) => !exampleFeatureFlags.enable
          case _ => false
        }
      }

    }

}
