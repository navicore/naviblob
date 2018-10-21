package onextent.akka.naviblob.azure

import java.nio.ByteBuffer

import com.microsoft.azure.storage.blob.BlobURL
import com.microsoft.rest.v2.util.FlowableUtil
import com.sksamuel.avro4s.{AvroInputStream, AvroSchema}
import io.reactivex.Single

class EhCaptureSetReader(path: String)(implicit cfg: AzureBlobConfig) extends AzureBlobber {

  val burl: BlobURL = containerURL.createBlobURL(path)
  val o: Single[ByteBuffer] = burl
    .download(null, null, false, null)
    .flatMap(r => FlowableUtil.collectBytesInBuffer(r.body(null)))

  def read(): Set[String] = {
    val bytes: ByteBuffer = o.blockingGet()
    val is: AvroInputStream[EhRecord] =
      AvroInputStream.data[EhRecord].from(bytes).build(AvroSchema[EhRecord])
    val records: Set[EhRecord] = is.iterator.toSet
    is.close()
    records.map(_.Body)
  }

}
