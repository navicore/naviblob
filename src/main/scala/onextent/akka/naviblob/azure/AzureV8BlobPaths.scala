package onextent.akka.naviblob.azure

import scala.collection.JavaConverters._

class AzureV8BlobPaths(implicit cfg: AzureBlobConfig)
    extends AzureV8Blobber
    with Iterable[String] {

  var paths = List()

  override def iterator: Iterator[String] = {

    val l = cfg.path match {
      case Some(p) =>
        logger.debug(s"listing blobs at path $p")
        val i = container.listBlobs(p)
        i.iterator()
      case _ => container.listBlobs().iterator()
    }

    l.asScala.map(_.getUri.getPath)

  }

}
