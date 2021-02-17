package $organization$.util

import monix.execution.Scheduler

object ThreadPools {

  val featureFlagsThreadPool = Scheduler.fixedPool(
    name = "feature-flags-thread-pool",
    poolSize = 4
  )

}
