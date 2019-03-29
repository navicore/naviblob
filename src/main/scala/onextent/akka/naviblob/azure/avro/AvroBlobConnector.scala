package onextent.akka.naviblob.azure.avro

import akka.actor.Props
import com.sksamuel.avro4s.{Decoder, SchemaFor}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.azure.BlobConnector
import onextent.akka.naviblob.azure.storage._

object AvroBlobConnector extends LazyLogging {
  val name: String = "AvroBlobConnector"
  def props[T >: Null: Decoder: SchemaFor](implicit config: BlobConfig) =
    Props(new AvroBlobConnector[T]())
}

class AvroBlobConnector[T >: Null: Decoder: SchemaFor](
    implicit config: BlobConfig)
    extends BlobConnector[T]
    with LazyLogging {

  def createIterator(path: String): Iterator[T] =
    new AvroStreamReader[T](path).read()

}
