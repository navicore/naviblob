package onextent.akka.naviblob.azure

import com.microsoft.azure.storage.blob.CloudBlockBlob
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}

class EhCaptureStreamReader(path: String)(implicit cfg: AzureBlobConfig)
    extends AzureV8Blobber {

  val blob: CloudBlockBlob = container.getBlockBlobReference(path)

  def read(): Iterator[EhRecord] = {

    val bis = blob.openInputStream()

    val is: AvroInputStream[EhRecord] =
      AvroInputStream.data[EhRecord].from(bis).build(AvroSchema[EhRecord])

    is.iterator

  }

}
