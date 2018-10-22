package onextent.akka.naviblob.azure.storage

import scala.collection.JavaConverters._

class V8BlobPaths(implicit cfg: BlobConfig)
    extends V8Blobber
    with Iterable[String] {

  var paths = List()

  // BROKEN
  // BROKEN
  // BROKEN
  // BROKEN the insane move to put a completely incompatible API over the same package namespace makes this hard to debug, 8.0 vs 10.1
  // BROKEN
  // BROKEN
  // BROKEN
  // BROKEN
  // BROKEN
  @deprecated
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
