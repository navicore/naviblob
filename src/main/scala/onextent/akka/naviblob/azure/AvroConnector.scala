package onextent.akka.naviblob.azure

import akka.actor.{Actor, Props}
import com.sksamuel.avro4s.{Decoder, SchemaFor}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.azure.AvroConnector.{NoMore, Pull}

object AvroConnector extends LazyLogging {

  val name: String = "AvroConnector"

  def props[T >: Null : Decoder : SchemaFor](implicit config: BlobConfig) = Props(new AvroConnector[T]())

  final case class Pull()
  final case class NoMore()
}

class AvroConnector[T >: Null : Decoder : SchemaFor](implicit config: BlobConfig)
    extends Actor
    with LazyLogging {

  val pathsIterator: Iterator[String] = new BlobPaths().toList.iterator

  var readerIterator: Iterator[EhRecord] =
    new AvroStreamReader[EhRecord](pathsIterator.next()).read()

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
