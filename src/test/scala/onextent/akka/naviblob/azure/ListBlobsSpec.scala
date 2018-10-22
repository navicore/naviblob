package onextent.akka.naviblob.azure

import org.scalatest._

class ListBlobsSpec extends FlatSpec with Matchers {

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  implicit val cfg: BlobConfig = BlobConfig(storageAccount, storageKey, containerName, storagePath)
  implicit val azureBlobber: Blobber = new Blobber

  ignore should "list blobs" in {

    val c = new BlobPaths
    c.foreach(u => println(s"ejs $u"))

  }

}
