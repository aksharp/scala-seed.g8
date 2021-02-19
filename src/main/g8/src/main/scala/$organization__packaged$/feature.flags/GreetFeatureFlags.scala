package $organization$.feature.flags

import $organization$.Main
import com.tremorvideo.lib.feature.flags.{FeatureFlags, FromBytes}
import io.circe.generic.auto._
import monix.eval.Task

case class GreetFeatureFlags(
                              allow: List[String],
                              block: List[String],
                              enable: Boolean
                            )

object GreetFeatureFlags extends FeatureFlags[Task, GreetFeatureFlags] {
  override def fromBytes(bytes: Array[Byte]): Either[Throwable, GreetFeatureFlags] =
    FromBytes[GreetFeatureFlags](bytes)

  override val observableQueue = Main.observableQueue
}