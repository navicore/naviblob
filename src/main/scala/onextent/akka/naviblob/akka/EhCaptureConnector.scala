package onextent.akka.naviblob.akka

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.akka.EhCaptureConnector.{NoMore, Pull}
import onextent.akka.naviblob.azure._

object EhCaptureConnector extends LazyLogging {
  val name: String = "EhCaptureConnector"
  def props(implicit config: AzureBlobConfig) =
    Props(new EhCaptureConnector())
  final case class Pull()
  final case class NoMore()
}

class EhCaptureConnector(implicit config: AzureBlobConfig)
    extends Actor
    with LazyLogging {

  val pathsIterator: Iterator[String] = new AzureBlobPaths().toList.iterator

  var readerIterator: Iterator[EhRecord] =
    new EhCaptureStreamReader(pathsIterator.next()).read()

  override def receive: Receive = {

    case _: Pull =>
      if (readerIterator.hasNext) {
        // read one from the current file
        sender() ! readerIterator.next().Body
      } else {
        // open next file and read one
        if (pathsIterator.hasNext) {
          readerIterator = new EhCaptureStreamReader(pathsIterator.next()).read()
          sender() ! readerIterator.next().Body
        } else {
          // all files in original path spec have been processed
          sender() ! NoMore()
        }
      }

    case x => logger.error(s"I don't know how to handle ${x.getClass.getName}")

  }

}
