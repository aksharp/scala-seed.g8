package $organization$.services

import $organization$._
import $organization$.config.AppConfig
import $organization$.feature.flags.GreetFeatureFlags
import $organization$.mocks._
import $organization$.test.util.TestUtils
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GreeterServiceSpec extends AnyWordSpec with Matchers with TestUtils {

  // required implicits - can override with values as need be
  implicit val observableAndTraceableService: ObservableAndTraceableService[Task] = anObservableAndTraceableService()
  implicit val observableAndTraceable: ObservableAndTraceable = anObservableAndTraceable()
  implicit val appConfig: AppConfig = anAppConfig()
  implicit val scheduler: Scheduler = Scheduler.global

  // service under test
  val greeterService = new GreeterServiceImpl

  "should receive WelcomeResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = true
      )
    )

    val response: GreetResponse = greeterService.process(
      aGreetRequest(name = "Alex")
    ).runSyncUnsafe()

    response should be(
      WelcomeResponse(
        message = "Hello, Alex!"
      )
    )
  }

  "should receive NotWelcomeResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List("Alex"),
        enable = true
      )
    )

    val response: GreetResponse = greeterService.process(
      aGreetRequest(name = "Alex")
    ).runSyncUnsafe()

    response should be(
      NotWelcomeResponse(
        message = s"Alex, you are not welcome here!"
      )
    )
  }

  "should receive OutOfServiceResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = false
      )
    )

    val response: GreetResponse = greeterService.process(
      aGreetRequest(name = "Alex")
    ).runSyncUnsafe()

    response should be(
      OutOfServiceResponse()
    )
  }

  "should receive ErrorResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = true
      )
    )

    val response: GreetResponse = greeterService.process(
      aGreetRequest(name = "")
    ).runSyncUnsafe()

    response should be(
      ErrorResponse(
        message = "name can't be empty"
      )
    )
  }

}
