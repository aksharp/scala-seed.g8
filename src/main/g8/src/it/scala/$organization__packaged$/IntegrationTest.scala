package $organization$

import $organization$._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

// todo: move to /it directory to run separately via sbt
class IntegrationTest extends AnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  "should return WelcomeResponse" in {

    val name = "Alex"

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = name
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = WelcomeResponse(
      message = "Hello, " + name + "!"
    )
    result should be(expectedResult)

  }

  "should get ErrorResponse when submitting with empty name" in {

    val emptyName = ""

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = emptyName
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = ErrorResponse(
      message = "name can't be empty"
    )
    result should be(expectedResult)

  }

}
