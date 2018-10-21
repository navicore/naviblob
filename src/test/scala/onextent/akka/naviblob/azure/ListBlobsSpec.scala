package onextent.akka.naviblob.azure

import org.scalatest._

class ListBlobsSpec extends FlatSpec with Matchers {

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  implicit val cfg: AzureBlobConfig = AzureBlobConfig(storageAccount, storageKey, containerName, storagePath)
  implicit val azureBlobber: AzureBlobber = new AzureBlobber

  ignore should "list blobs" in {

    val c = new AzureBlobPaths
    c.foreach(u => println(s"ejs $u"))

  }

  ignore should "read blob" in {

    val c = new AzureBlobPaths
    c.toList.headOption match {
      case Some(p) =>
        val r = new EhCaptureSetReader(p)
        val records = r.read()
        records.size should be(12688)
        records.slice(0, 10).foreach(println)
      case _ =>
    }

  }
}
