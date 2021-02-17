package $organization$.config

import com.tremorvideo.lib.feature.flags.{Debug, Observe}
import com.typesafe.scalalogging.LazyLogging
import pureconfig.{ConfigReader, ConfigSource}

import scala.sys.exit
import pureconfig.generic.auto._
import pureconfig.generic.semiauto._

object AppConfigLoader extends LazyLogging {

  implicit val debugConvert: ConfigReader[Debug] = deriveEnumerationReader[Debug]
  implicit val observeConvert: ConfigReader[Observe] = deriveEnumerationReader[Observe]

  def loadOrExitWithErrorMessage(
                                  args: Array[String]
                                ): AppConfig = {
    args
      .headOption
      .map { env =>
        val config: AppConfig = ConfigSource
          .resources(s"${env}.conf")
          .load[AppConfig]
          .fold(
            failure => {
              val errorMessage = failure.toList.map(_.description).mkString(", ")
              logger.error(s"Unable to deserialize AppConfig ERROR: ${errorMessage}")
              exit(0)
            },
            good => good
          )
        config
      }
      .getOrElse {
        logger.error(s"no environment for config specified")
        exit(0)
      }
  }
}
