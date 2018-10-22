package onextent.akka.naviblob.azure.avvro

import akka.actor.{Actor, Props}
import com.sksamuel.avro4s.{Decoder, SchemaFor}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.akka.{NoMore, Pull}
import onextent.akka.naviblob.azure.storage.{BlobConfig, BlobPaths}

object AvroConnector extends LazyLogging {

  val name: String = "AvroConnector"

  def props[T >: Null : Decoder : SchemaFor](implicit config: BlobConfig) = Props(new AvroConnector[T]())
}

class AvroConnector[T >: Null : Decoder : SchemaFor](implicit config: BlobConfig)
    extends Actor
    with LazyLogging {

  val pathsIterator: Iterator[String] = new BlobPaths().toList.iterator

  var readerIterator: Iterator[T] = new AvroStreamReader[T](pathsIterator.next()).read()

  override def receive: Receive = {

    case _: Pull =>
      if (readerIterator.hasNext) {
        // read one from the current file
        sender() ! readerIterator.next()
      } else {
        // open next file and read one
        if (pathsIterator.hasNext) {
          readerIterator = new AvroStreamReader[T](pathsIterator.next()).read()
          sender() ! readerIterator.next()
        } else {
          // all files in original path spec have been processed
          sender() ! NoMore()
        }
      }

    case x => logger.error(s"I don't know how to handle ${x.getClass.getName}")

  }

}
