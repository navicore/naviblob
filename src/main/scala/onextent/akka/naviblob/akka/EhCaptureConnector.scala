package onextent.akka.naviblob.akka

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.akka.EhCaptureConnector.Pull
import onextent.akka.naviblob.azure.{AzureBlobConfig, AzureBlobPaths, EhCaptureStreamReader, EhRecord}

object EhCaptureConnector extends LazyLogging {

  val name: String = "ConnectorActor"
  def props(implicit config: AzureBlobConfig) =
    Props(new EhCaptureConnector())
  final case class Pull()

}

class EhCaptureConnector(implicit config: AzureBlobConfig)
    extends Actor
    with LazyLogging {

  val pathsIterator: Iterator[String] = new AzureBlobPaths().toList.iterator
  var readerIterator: Iterator[EhRecord] = new EhCaptureStreamReader(pathsIterator.next()).read()

  override def receive: Receive = {

    case _: Pull =>
      logger.debug("connector onPull")
      // todo: get one from the iterator or create new iterator
      if (readerIterator.hasNext) {
        sender() ! readerIterator.next().Body
      } else {
        // todo: next file
      }

    case x => logger.error(s"I don't know how to handle ${x.getClass.getName}")

  }

}
