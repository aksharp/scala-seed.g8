package $organization$.util

import com.typesafe.scalalogging.LazyLogging

trait LogInitialization extends LazyLogging {
  self =>
  logger.info(s"$"$"${self.getClass.getName} initialized")
}
