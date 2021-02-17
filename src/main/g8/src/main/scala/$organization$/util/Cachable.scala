package $organization$.util

import com.typesafe.scalalogging.LazyLogging
import java.util
import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, Executors, ScheduledExecutorService, _}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

trait PopulateCachePoller[A, B] {
  val cachableService: CachableService[A, B]
  val poll: () => Seq[(A, B)]
  val schedulerDelay: FiniteDuration

  val schedulerThreads: Int = 1

  private val s: ScheduledExecutorService = Executors.newScheduledThreadPool(schedulerThreads)

  s.schedule(new Runnable {
    override def run(): Unit = poll().foreach {
      case (a, b) => cachableService.addToCache(a, b)
    }
  }, schedulerDelay._1, schedulerDelay._2)
}


trait PopulateCacheQueue[A, B] {
  val queueCapacity: Int
  lazy val queue: BlockingQueue[(A, B)] = new ArrayBlockingQueue(queueCapacity)
  def addToQueue(a: A, b: B): Unit = queue.put(a, b)
  val cachableService: CachableService[A, B]

  def rec(q: BlockingQueue[(A, B)]): Unit = {
    if (q.size() > 0) {
      val collection = new util.ArrayList[(A, B)]();
      q.drainTo(collection)
      collection.asScala.foreach {
        case (a, b) => cachableService.addToCache(a, b)
      }
    }
    rec(q)
  }

}

object CachableService {
  implicit def unwrapSet[A](set: Option[Set[A]]): Set[A] = set.getOrElse(Set.empty)
}

trait CachableService[KEY, VALUE] extends LazyLogging {
  protected val warmupType: WarmupType = NoWarmup
  protected val keysToWarmup: Seq[KEY] = Seq.empty[KEY]
  protected implicit val ec: ExecutionContext = ExecutionContext.global

  def warmup(): Unit = warmupType match {
    case FastStart => nonBlockingWarmup(keysToWarmup)
    case WarmupAll => blockingWarmup(keysToWarmup)
    case NoWarmup => ()
  }

  private val cache: ConcurrentHashMap[KEY, VALUE] = new ConcurrentHashMap[KEY, VALUE]()

  def isWarmedUp(minCacheSize: Int) = cache.size() > minCacheSize

  private def blockingWarmup(list: Seq[KEY]): Unit = {
    logger.info(s"### START WARMUP")
    list.foreach(getFromCacheOrBlockToQueryAndAddToCache)
    logger.info(s"### END WARMUP")
  }

  private def nonBlockingWarmup(list: Seq[KEY])(implicit ec: ExecutionContext): Unit = {
    logger.info(s"### START FAST STARTUP WARMUP")
    if (list.nonEmpty)
      list.foreach(getFromCacheOrCacheMissAndNonBlockingQueryToPopulateCache)
    else
      logger.info(s"### NO KEYS SELECTED FOR WARMUP. DONE.")
  }

  def getFromCacheOrCacheMissAndNonBlockingQueryToPopulateCache(key: KEY)(implicit ec: ExecutionContext): Option[VALUE] = {
    getFromCache(key)
      .map { value =>
        value
      }
      .orElse {
        for {
          maybeValue <- Future(query(key))
        } yield {
          for {
            value <- maybeValue
          } yield {
            addToCache(key, value)
            value
          }
        }
        None
      }
  }
  def getFromCacheOrBlockToQueryAndAddToCache(key: KEY): Option[VALUE] = {
    getFromCache(key)
      .map { b =>
        Option(b)
      }
      .getOrElse {
        val b = query(key)
        for {
          value <- query(key)
        } yield {
          addToCache(key, value)
          value
        }
      }
  }
  protected def query(a: KEY): Option[VALUE]
  private def getFromCache(a: KEY): Option[VALUE] = if (cache.containsKey(a)) Option(cache.get(a)) else None
  def addToCache(a: KEY, b: VALUE): Unit = {
    cache.put(a, b)
  }
}

sealed trait WarmupType
case object FastStart extends WarmupType
case object WarmupAll extends WarmupType
case object NoWarmup extends WarmupType

object WarmUp {
  //  def apply[C <: CachableService[K, V], K, V](cachableService: C): C = {
  def apply[C <: { def warmup(): Unit }](cachableService: C): C = {
    cachableService.warmup()
    //TODO: iterate until cachableService.isWarmedUp(expectedMinimumCacheSize) is true or timeout has been reached
    // return either "warmed up" or "timed out and % warmed up" in addition to cachableService
    cachableService
  }
}