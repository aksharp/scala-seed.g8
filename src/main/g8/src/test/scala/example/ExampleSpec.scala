package example

import $organization$._
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class ExampleSpec extends WordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  // TODO: start server before testing. Unignore test afterwards

  "greeter.greet test. Example test against running server returning default empty GreetResponse()" ignore {

    val name = "Alex"

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = name
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = GreetResponse(
      message = s"Hello, ${name}"
    )
    result should be(expectedResult)

  }

}
