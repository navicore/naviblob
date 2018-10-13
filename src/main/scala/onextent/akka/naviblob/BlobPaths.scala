package onextent.akka.naviblob

import java.net.URL
import java.util.Locale

import com.microsoft.azure.storage.blob._
import com.microsoft.rest.v2.http.HttpPipeline
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

class BlobPaths(accountName: String,
                accountKey: String,
                containerName: String,
                path: Option[String])
    extends Iterable[String]
    with LazyLogging {

  val credential = new SharedKeyCredentials(accountName, accountKey)

  val pipeline: HttpPipeline =
    StorageURL.createPipeline(credential, new PipelineOptions)

  val u = new URL(
    String.format(Locale.ROOT,
                  s"https://$accountName.blob.core.windows.net",
                  accountName))

  val serviceURL = new ServiceURL(u, pipeline)

  val containerURL: ContainerURL = serviceURL.createContainerURL(containerName)

  val options: ListBlobsOptions = new ListBlobsOptions()
  options.withMaxResults(100)
  path match {
    case Some(p) => options.withPrefix(p)
    case _ =>
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

  override def iterator: Iterator[String] =
    new Iterator[String] {
      def hasNext: Boolean = paths.nonEmpty
      def next: String = {
        val h = paths.head
        paths = paths.tail
        h
      }
    }

}
