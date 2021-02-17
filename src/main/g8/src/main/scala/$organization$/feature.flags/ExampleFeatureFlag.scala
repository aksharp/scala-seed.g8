package $organization$.feature.flags

import $organization$.Main
import com.tremorvideo.lib.feature.flags.{FeatureFlags, FromBytes}
import io.circe.generic.auto._
import monix.eval.Task

case class ExampleFeatureFlags(
                                  allow: List[String],
                                  block: List[String],
                                  enable: Boolean
                                )

object ExampleFeatureFlags extends FeatureFlags[Task, ExampleFeatureFlags] {
  override def fromBytes(bytes: Array[Byte]): Either[Throwable, ExampleFeatureFlags] =
    FromBytes[ExampleFeatureFlags](bytes)

  override val observableQueue = Main.observableQueue
}