package onextent.akka.naviblob.azure

import com.typesafe.scalalogging.LazyLogging
import org.scalatest._

class ListBlobsV8Spec extends FlatSpec with Matchers with LazyLogging {

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  implicit val cfg: AzureBlobConfig = AzureBlobConfig(storageAccount, storageKey, containerName, storagePath)
  implicit val azureBlobber: AzureV8Blobber = new AzureV8Blobber()

  ignore should "read blob" in {

    new AzureBlobPaths().toList.headOption match {
      case Some(p) =>

        val r = new EhCaptureStreamReader(p)
        val iter = r.read()
        val records = iter.toList

        records.size should be(9997)
        records.slice(0, 10).foreach(println)

      case _ => assertResult(false)(true)
    }

  }

}
