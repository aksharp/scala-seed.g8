package $organization$.config

import com.tremorvideo.lib.feature.flags.{Debug, FeatureFlagsConfig, Observe}
import com.tremorvideo.lib.kafka.producer.TremorKafkaProducerConfig

case class AppConfig(
                      consulDynamicConfig: ConsulDynamicConfig,
                      appName: String,
                      dataCenter: String,
                      debug: Debug,
                      observe: Observe,
                      grpcServerPort: Int,
                      observableStaticConfigProducer: TremorKafkaProducerConfig,
                      observableDynamicConfigProducer: TremorKafkaProducerConfig
                    ) extends FeatureFlagsConfig

case class ConsulDynamicConfig(
                                consulHostname: String,
                                consulPort: Int
                              )
