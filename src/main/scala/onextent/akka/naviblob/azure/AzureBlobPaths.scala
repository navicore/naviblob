package onextent.akka.naviblob.azure

import com.microsoft.azure.storage.blob._

import scala.annotation.tailrec

class AzureBlobPaths(implicit cfg: AzureBlobConfig)
    extends AzureBlobber
    with Iterable[String] {

  //confirmed that java is mutating the options
  options.withMaxResults(100)
  cfg.path match {
    case Some(p) => options.withPrefix(p)
    case _       =>
  }

  var paths: List[String] = List()

  @tailrec
  private def getSegment(marker: String,
                         options: ListBlobsOptions): List[String] = {

    val o = containerURL.listBlobsFlatSegment(marker, options, null)
    val body = o.blockingGet().body()
    val segment = body.segment()

    if (segment != null)
      segment
        .blobItems()
        .forEach(y => paths = y.name() :: paths)

    paths = paths.reverse
    val nextMarer = body.nextMarker()
    if (nextMarer == null) {
      List()
    } else {
      getSegment(nextMarer, options)
    }

  }

  getSegment(null, options)

  override def iterator: Iterator[String] = {
    paths = paths.sorted
    new Iterator[String] {
      def hasNext: Boolean = paths.nonEmpty
      def next: String = {
        val h = paths.head
        paths = paths.tail
        h
      }
    }
  }

}
