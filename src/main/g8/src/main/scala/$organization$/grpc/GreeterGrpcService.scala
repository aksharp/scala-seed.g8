package $organization$.grpc

import scala.concurrent.Future
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService

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

//  If this is the first entry, and no ObservableAndTraceable are coming in on request, do this:
//    (for {
//      implicit0(ot: ObservableAndTraceable) <- observableAndTraceableService.newObservableAndTraceable()
//      greetResponse <- greeterService.process(greetRequest)
//    } yield {
//      greetResponse
//    }).runToFuture(global)

  }
}

