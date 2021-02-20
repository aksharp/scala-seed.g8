package $organization$.feature.flags

import $organization$.Main
import $organization$.feature.flags.setup.FeatureFlagsPollerMonixImpl
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import $organization$.test.util.TestUtils

class FeatureFlagsSpec extends AnyWordSpec with Matchers with Eventually with TestUtils {

  "test all feature flags" in {

    //    Seq("eu1", "iad1", "ap1").foreach(dc => {
    val pollerAndObserver = new FeatureFlagsPollerMonixImpl()
    val poller = pollerAndObserver.run(
      appConfig = anAppConfig(), //todo: this is iad1 specific. need to refactor AppConfig,
      observableQueue = anObservableQueue(10),
      supportedFeatureFlags = Main.supportedFeatureFlags
    ).runSyncUnsafe()

    poller.exceptions should be(List.empty)


    //    })

  }


}

