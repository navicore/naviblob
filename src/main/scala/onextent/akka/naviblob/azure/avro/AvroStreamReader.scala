package onextent.akka.naviblob.azure.avro

import com.microsoft.azure.storage.blob.CloudBlockBlob
import com.sksamuel.avro4s._
import onextent.akka.naviblob.azure.storage.{BlobConfig, Blobber}

class AvroStreamReader[T >: Null : Decoder : SchemaFor](path: String)(implicit cfg: BlobConfig)
    extends Blobber {

  val blob: CloudBlockBlob = container.getBlockBlobReference(path)

  def read(): Iterator[T] = {

    val bis = blob.openInputStream()

    val is: AvroInputStream[T] = AvroInputStream.data[T].from(bis).build(AvroSchema[T])

    is.iterator

  }

}
