package onexent.akka.naviblob.akka

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import onextent.akka.naviblob.akka.NaviBlob
import onextent.akka.naviblob.azure.{AvroConnector, BlobConfig, EhRecord}
import org.scalatest._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class StreamSpec extends FlatSpec with Matchers {

  implicit val actorSystem: ActorSystem = ActorSystem("spec")
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(actorSystem))

  def requestDuration: Duration = {
    val t = "120 seconds"
    Duration(t)
  }
  implicit def requestTimeout: Timeout = {
    val d = requestDuration
    FiniteDuration(d.length, d.unit)
  }

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  var count = 0
  val consumer: Sink[String, Future[Done]] = Sink.foreach(m => {
    count += 1
    println(s"$count sunk $m")
  })

  "stream" should "read blobs" in {

    implicit val cfg: BlobConfig = BlobConfig(storageAccount, storageKey, containerName, storagePath)
    val connector: ActorRef = actorSystem.actorOf(AvroConnector.props[EhRecord])
    val srcGraph = new NaviBlob(connector)

    val r: Future[Done] = Source.fromGraph(srcGraph).runWith(consumer)

    Await.result(r, 10 * 60 seconds)

  }

}
