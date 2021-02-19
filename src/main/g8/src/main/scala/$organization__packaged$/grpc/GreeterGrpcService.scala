package $organization$.grpc

import scala.concurrent.Future
import $organization$._
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import monix.eval.Task
import monix.execution.Scheduler.global

class GreeterGrpcService(
                          greeterService: services.GreeterService[Task]
                        )
                        (
                          implicit observableAndTraceableService: ObservableAndTraceableService[Task]
                        ) extends GreeterGrpc.Greeter {

  override def greet(greetRequest: GreetRequest): Future[GreetResponse] = {

    implicit val ot: ObservableAndTraceable = greetRequest.observableAndTraceable

    greeterService
      .process(greetRequest)
      .runToFuture(global)
  }

}

