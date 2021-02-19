package $organization$.http

import monix.eval.Task
import org.http4s.dsl.Http4sDsl

object AppHttp4sDsl extends Http4sDsl[Task]