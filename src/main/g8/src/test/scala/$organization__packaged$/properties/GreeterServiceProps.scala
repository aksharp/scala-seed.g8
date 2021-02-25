package $organization$.properties

import $organization$.config.AppConfig
import $organization$.feature.flags.GreetFeatureFlags
import com.tremorvideo.lib.api.observable.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import $organization$.test.util.TestUtils
import $organization$._
import monix.eval.Task
import monix.execution.Scheduler
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.concurrent.Await
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
  val greeterService = aGreeterService

  property(
    """
      FEATURE FLAGS: [GreetFeatureFlags]
      INPUT: [GreetRequest]
      OUTPUT: [GreetResponse]
        EITHER [WelcomeResponse] // happy path type
        OR [NotWelcomeResponse]
        OR [OutOfServiceResponse]
        OR [ErrorResponse]
      """.stripMargin
  ) =
    forAll {
      (
        greetFeatureFlags: GreetFeatureFlags, // 100 times generated GreetFeatureFlags values (details in arbitraries)
        input: GreetRequest // 100 times generated GreetRequest values (details in arbitraries)
      ) => {
        GreetFeatureFlags.set(greetFeatureFlags) // set feature flags with generated value

        val future = for {
          greetResponse <- greeterService
            .greet( // testing properties of this function
              greetRequest = input // pass generated input value
            )
        } yield {
          greetResponse match {
            case WelcomeResponse(_) => greetFeatureFlags.enable && !greetFeatureFlags.block.contains(input.name)
            case NotWelcomeResponse(_) => greetFeatureFlags.enable && greetFeatureFlags.block.contains(input.name)
            case OutOfServiceResponse(_) => !greetFeatureFlags.enable
            case ErrorResponse(_) => input.name.isEmpty
            case _ => false
          }
        }

        Await.result(future, Duration.Inf)

      }
    }
}
