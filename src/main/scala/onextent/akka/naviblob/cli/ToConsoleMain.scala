package onextent.akka.naviblob.cli

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout
import onextent.akka.naviblob.akka.NaviBlob
import onextent.akka.naviblob.azure.avro.{AvroBlobConnector, EhRecord}
import onextent.akka.naviblob.azure.storage.BlobConfig

import scala.concurrent.duration._
import scala.concurrent._
import scala.language.postfixOps

object ToConsoleMain extends App {

  val mode = sys.env.getOrElse("MODE", "avro")
  val patterns: List[String] =
    sys.env.getOrElse("PATTERNS", "").split(" ").toList

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  println(
    s"mode: $mode storeageAccount: $storageAccount storageKey: $storageKey " +
      s"storagePath: $storagePath containerName: $containerName patterns: $patterns")

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

  val consumer: Sink[EhRecord, Future[Done]] = Sink.foreach(m => {
    println(s"from enqueue time ${m.EnqueuedTimeUtc} found ${m.Properties}")
    println(s"${m.Body}")
  })

  for (pid <- 0 until 4) {

    implicit val cfg: BlobConfig =
      BlobConfig(storageAccount, storageKey, containerName, storagePath.map(_.replace("{0}", s"$pid")))

    val connector: ActorRef =
      actorSystem.actorOf(AvroBlobConnector.props[EhRecord])

    val src = NaviBlob[EhRecord](connector)

    src
      .filter(r => {

        patterns.exists(e => r.Body.contains(e))

      })
      .runWith(consumer)

  }

}
