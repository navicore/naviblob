package onextent.akka.naviblob.azure.text

import java.io.{BufferedReader, InputStreamReader}
import java.util.zip.GZIPInputStream

import com.microsoft.azure.storage.blob.CloudBlockBlob
import onextent.akka.naviblob.azure.storage.{BlobConfig, Blobber}

class GzipTextStreamReader(path: String)(implicit cfg: BlobConfig) extends Blobber {

  val blob: CloudBlockBlob = container.getBlockBlobReference(path)

  def read(): Iterator[String] = {

    val bis = new GZIPInputStream(blob.openInputStream())

    val reader = new BufferedReader(new InputStreamReader(bis))

    val iter = reader.lines().iterator()

    new Iterator[String] {

      override def hasNext: Boolean = {
        val r =iter.hasNext
        if (!r) reader.close()
        r
      }

      override def next(): String = iter.next()

    }

  }


}
