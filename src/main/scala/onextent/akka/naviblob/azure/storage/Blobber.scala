package onextent.akka.naviblob.azure.storage

import java.net.URL
import java.util.Locale

import com.microsoft.azure.storage.blob._
import com.microsoft.rest.v2.http.HttpPipeline
import com.typesafe.scalalogging.LazyLogging

class Blobber(implicit cfg: BlobConfig) extends LazyLogging {

  val credential = new SharedKeyCredentials(cfg.accountName, cfg.accountKey)

  val pipeline: HttpPipeline =
    StorageURL.createPipeline(credential, new PipelineOptions)

  val u = new URL(
    String.format(Locale.ROOT,
                  s"https://${cfg.accountName}.blob.core.windows.net",
                  cfg.accountName))

  val serviceURL = new ServiceURL(u, pipeline)

  val containerURL: ContainerURL =
    serviceURL.createContainerURL(cfg.containerName)

  val options: ListBlobsOptions = new ListBlobsOptions()

}
