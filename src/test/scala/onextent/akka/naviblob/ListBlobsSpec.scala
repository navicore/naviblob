package onextent.akka.naviblob

import org.scalatest._

class ListBlobsSpec extends FlatSpec {

  //val storageConnectionString: String = s"DefaultEndpointsProtocol=https;AccountName=${sys.env.getOrElse("BLOB_ACCOUNT", "unknown")};AccountKey=${sys.env.getOrElse("BLOB_KEY", "unknown")}"

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  "api" should "list blobs" in {

    val c = new BlobPaths(storageAccount, storageKey, containerName, storagePath)
    c.foreach(u => println(s"ejs $u"))

  }

}
