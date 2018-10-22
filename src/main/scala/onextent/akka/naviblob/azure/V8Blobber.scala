package onextent.akka.naviblob.azure

import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.blob.{CloudBlobClient, CloudBlobContainer}
import com.typesafe.scalalogging.LazyLogging

class V8Blobber(implicit cfg: BlobConfig) extends LazyLogging {

  val storageConnectionString =
    s"DefaultEndpointsProtocol=https;AccountName=${cfg.accountName};AccountKey=${cfg.accountKey}"
  val storageAccount: CloudStorageAccount =
    CloudStorageAccount.parse(storageConnectionString)
  val blobClient: CloudBlobClient = storageAccount.createCloudBlobClient()
  val container: CloudBlobContainer =
    blobClient.getContainerReference(cfg.containerName)
}
