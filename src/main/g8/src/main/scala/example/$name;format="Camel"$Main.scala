package example

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

object ExampleMain extends App {

  implicit val ec: ExecutionContext = global

  server.run(
    greeter = new GreeterService
  )

}

class GreeterService extends GreeterGrpc.Greeter {
  override def greet(req: GreetRequest): Future[GreetResponse] = {
    Future.successful(GreetResponse(
      message = s"Hello, ${req.name}!"
    ))
  }
}

