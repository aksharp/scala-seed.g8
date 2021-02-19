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
import org.scalacheck.Properties

import scala.concurrent.duration.Duration

object GreeterServiceProps extends Properties("GreeterServiceProps") with TestUtils {

  // required implicits - can override with values as need be
  implicit val observableAndTraceableService: ObservableAndTraceableService[Task] = anObservableAndTraceableService()
  implicit val observableAndTraceable: ObservableAndTraceable = anObservableAndTraceable()
  implicit val appConfig: AppConfig = anAppConfig()
  implicit val scheduler: Scheduler = Scheduler.global
  // code generated arbitraries
  import $organization$.mocks.Arbitraries._

  // service under test
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
        exampleFeatureFlags: ExampleFeatureFlags, // 100 times generated ExampleFeatureFlags values (details in arbitraries)
        input: GreetRequest // 100 times generated GreetRequest values (details in arbitraries)
      ) => {
        ExampleFeatureFlags.set(exampleFeatureFlags) // set feature flags with generated value
        val greetResponse: GreetResponse =
          greeterService
            .process( // testing properties of this function
              greetRequest = input // pass generated input value
            )
            .runSyncUnsafe(Duration.Inf) // run synchronously

        // properties of various responses
        greetResponse match {
          case WelcomeResponse(_) => exampleFeatureFlags.enable && !exampleFeatureFlags.block.contains(input.name)
          case NotWelcomeResponse(_) => exampleFeatureFlags.enable && exampleFeatureFlags.block.contains(input.name)
          case OutOfServiceResponse(_) => !exampleFeatureFlags.enable
          case _ => false
        }
      }

    }

}
