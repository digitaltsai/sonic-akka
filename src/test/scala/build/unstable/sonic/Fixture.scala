package build.unstable.sonic

import java.io.File
import java.nio.file.WatchEvent.Kind
import java.nio.file.{Path, WatchEvent}

import akka.actor.{Actor, Props}
import akka.stream.actor.ActorPublisher
import akka.testkit.CallingThreadDispatcher
import build.unstable.sonic.model._
import build.unstable.sonic.scaladsl.Sonic
import build.unstable.sonic.server.source.SyntheticPublisher
import spray.json._

object Fixture {

  val config = """{"class" : "SyntheticSource"}""".parseJson.asJsObject
  val traceId = "1234"
  val syntheticQuery = Query("10", config, None)
    .copy(query_id = Some(1), trace_id = Some(traceId))

  val queryBytes = Sonic.lengthPrefixEncode(syntheticQuery.toBytes)

  // in memory db
  val H2Driver = "org.h2.Driver"

  val testUser = ApiUser("serrallonga", 10, AuthConfig.Mode.ReadWrite, None)

  val testCtx = RequestContext("1", Some(testUser))

  val syntheticPubProps = Props(classOf[SyntheticPublisher], None, Some(1), 10, "1", false, None, testCtx)
    .withDispatcher(CallingThreadDispatcher.Id)

  val zombiePubProps = Props[Zombie].withDispatcher(CallingThreadDispatcher.Id)

  val tmp = new File("/tmp/sonicd_specs")
  val tmp2 = new File("/tmp/sonicd_specs/recursive")
  val tmp3 = new File("/tmp/sonicd_specs/recursive/rec2")
  val tmp32 = new File("/tmp/sonicd_specs/recursive/rec2/rec2")
  val tmp4 = new File("/tmp/sonicd_specs/recursive2")
  val file = new File("/tmp/sonicd_specs/recursive/tmp.txt")
  val file2 = new File("/tmp/sonicd_specs/recursive/rec2/tmp.txt")
  val file3 = new File("/tmp/sonicd_specs/logback.xml")
  val file4 = new File("/tmp/sonicd_specs/logback2.xml")

  def getEvent(k: Kind[Path], path: Path) = new WatchEvent[Path] {
    override def count(): Int = 1

    override def kind(): Kind[Path] = k

    override def context(): Path = path
  }
}

class Zombie extends ActorPublisher[SonicMessage] {
  override def receive: Actor.Receive = {
    case any ⇒ //ignore
  }
}
