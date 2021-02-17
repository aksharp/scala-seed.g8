package $organization$.services

import com.typesafe.scalalogging.LazyLogging

trait LoggableService extends LazyLogging {
  self =>
  logger.info(s"${self.getClass.getName} initialized")
}
