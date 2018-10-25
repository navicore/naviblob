package onextent.akka.naviblob.azure.storage

import scala.collection.JavaConverters._

class BlobPaths(implicit cfg: BlobConfig)
    extends Blobber
    with Iterable[String] {

  var paths = List()

  override def iterator: Iterator[String] = {

    val l = cfg.path match {
      case Some(p) =>
        logger.debug(s"listing blobs at path $p")
        val i = container.listBlobs(p, true)
        i.iterator()
      case _ => container.listBlobs().iterator()
    }

    // wtf, there is no api to get the string that the rest of the api calls require :|
    l.asScala.map(x => x.getUri.getPath.replaceFirst(s"/${x.getContainer.getName}/", ""))

  }

}
