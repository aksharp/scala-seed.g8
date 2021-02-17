package $organization$.grpc

import scala.concurrent.Future
import $organization$.services._
import monix.eval.Task
import monix.execution.Scheduler.global

class GreeterGrpcService(
                          greeterService: GreeterService[Task]
                        ) extends GreeterGrpc.Greeter {
  override def greet(greetRequest: GreetRequest): Future[GreetResponse] = {

    (for {
      greetResponse <- greeterService.process(
        greetRequest = greetRequest
      )
    } yield {
      greetResponse
    }).runToFuture(global)

  }
}

