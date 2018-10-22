package onexent.akka.naviblob.akka

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout

import scala.concurrent.duration._
import onextent.akka.naviblob.akka.{EhCaptureConnector, NaviBlob}
import onextent.akka.naviblob.azure.AzureBlobConfig
import org.scalatest._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}

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

  ignore should "read blobs" in {

    implicit val cfg: AzureBlobConfig = AzureBlobConfig(storageAccount, storageKey, containerName, storagePath)
    val connector: ActorRef = actorSystem.actorOf(EhCaptureConnector.props(cfg))
    val srcGraph = new NaviBlob(connector)

    val r: Future[Done] = Source.fromGraph(srcGraph).runWith(consumer)

    Await.result(r, 10 * 60 seconds)

  }

}
