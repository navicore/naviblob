package onextent.akka.naviblob.akka

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout
import onextent.akka.naviblob.azure.avro.{AvroBlobConnector, EhRecord}
import onextent.akka.naviblob.azure.storage.BlobConfig
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class AvroStreamSpec extends FlatSpec with Matchers {

  implicit val actorSystem: ActorSystem = ActorSystem("spec")
  implicit val materializer: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem))

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
  val consumer: Sink[EhRecord, Future[Done]] = Sink.foreach(m => {
    count += 1
    println(s"$count sunk\n${m.Body}")
  })

  ignore should "read blobs" in {

    implicit val cfg: BlobConfig =
      BlobConfig(storageAccount, storageKey, containerName, storagePath)

    val connector: ActorRef =
      actorSystem.actorOf(AvroBlobConnector.props[EhRecord])

    val src = NaviBlob[EhRecord](connector)
    val r: Future[Done] = src.runWith(consumer)

    Await.result(r, 10 * 60 seconds)

  }

}
