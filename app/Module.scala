import com.google.inject.AbstractModule
import services.{ClusterPublisherService, MqttConnector}
import play.api.Logger
import services.ClusterService

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[MqttConnector]).asEagerSingleton()
    bind(classOf[ClusterService]).asEagerSingleton()
    bind(classOf[ClusterPublisherService]).asEagerSingleton()
  }
}
