import play.sbt.PlayRunHook
import sbt._
import java.net.InetSocketAddress
import scala.sys.process.Process

object Yarn {
  def apply(base: File): PlayRunHook = {

    object YarnProcess extends PlayRunHook {

      var watchProcess: Option[Process] = None

      override def beforeStarted(): Unit = {}

      override def afterStarted(addr: InetSocketAddress): Unit = {
        watchProcess = Some(Process("yarn watch", base).run)
      }

      override def afterStopped(): Unit = {
        watchProcess.map(p => p.destroy())
        watchProcess = None
      }
    }

    YarnProcess
  }
}
