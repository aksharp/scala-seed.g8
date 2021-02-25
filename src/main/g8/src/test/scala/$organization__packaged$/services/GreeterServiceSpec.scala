package $organization$.services

import $organization$.config.AppConfig
import $organization$.feature.flags.GreetFeatureFlags
import com.tremorvideo.lib.api.observable.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import $organization$.test.util.TestUtils
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.tremorvideo.lib.api.example._
import com.tremorvideo.lib.api.example.mocks._

class GreeterServiceSpec extends AnyWordSpec with Matchers with TestUtils {

  // required implicits - can override with values as need be
  implicit val observableAndTraceableService: ObservableAndTraceableService[Task] = anObservableAndTraceableService()
  implicit val observableAndTraceable: ObservableAndTraceable = anObservableAndTraceable()
  implicit val appConfig: AppConfig = anAppConfig()
  implicit val scheduler: Scheduler = Scheduler.global

  // service under test
  val greeterService = aGreeterService

  "should receive WelcomeResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = true
      )
    )

    for {
      response <- greeterService.greet(
        aGreetRequest(name = "Alex")
      )
    } yield {
      eventually {
        response should be(
          WelcomeResponse(
            message = "Hello, Alex!"
          )
        )
      }
    }


  }

  "should receive NotWelcomeResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List("Alex"),
        enable = true
      )
    )

    for {
      response <- greeterService.greet(
        aGreetRequest(name = "Alex")
      )
    } yield {
      eventually {
        response should be(
          NotWelcomeResponse(
            message = s"Alex, you are not welcome here!"
          )
        )
      }
    }
  }

  "should receive OutOfServiceResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = false
      )
    )

    for {
      response <- greeterService.greet(
        aGreetRequest(name = "Alex")
      )
    } yield {
      eventually {
        response should be(
          OutOfServiceResponse()
        )
      }
    }
  }

  "should receive ErrorResponse" in {
    GreetFeatureFlags.set(
      featureFlags = GreetFeatureFlags(
        allow = List.empty,
        block = List.empty,
        enable = true
      )
    )

    for {
      response <- greeterService.greet(
        aGreetRequest(name = "")
      )
    } yield {
      eventually {
        response should be(
          ErrorResponse(
            message = "name can't be empty"
          )
        )
      }
    }
  }

}
