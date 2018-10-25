package onextent.akka.naviblob.azure

import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.azure.avro.AvroStreamReader
import onextent.akka.naviblob.azure.storage.{BlobConfig, BlobPaths, Blobber}
import org.scalatest._

class ListBlobsV8Spec extends FlatSpec with Matchers with LazyLogging {

  val storageAccount: String = sys.env.getOrElse("BLOB_ACCOUNT", "unknown")
  val storageKey: String = sys.env.getOrElse("BLOB_KEY", "unknown")
  val storagePath: Option[String] = sys.env.get("BLOB_PATH")
  val containerName: String = sys.env.getOrElse("BLOB_CONTAINER", "unknown")

  ignore should "read blob" in {

    implicit val cfg: BlobConfig = BlobConfig(storageAccount, storageKey, containerName, storagePath)
    implicit val azureBlobber: Blobber = new Blobber()

    new BlobPaths().toList.headOption match {
      case Some(p) =>

        val r = new AvroStreamReader[EhRecord](p)
        val iter = r.read()
        val records = iter.toList

        records.size should be(12404)
        records.slice(0, 10).foreach(println)

      case _ => assertResult(false)(true)
    }

  }

}
