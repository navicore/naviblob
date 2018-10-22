package onextent.akka.naviblob.akka

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.akka.EhCaptureConnector.{NoMore, Pull}

import scala.concurrent.{Await, Future}

/**
  * Entry point api. Users instantiate this and wire it into their streams.
  */
class NaviBlob(connector: ActorRef)(implicit system: ActorSystem, to: Timeout)
    extends GraphStage[SourceShape[String]]
    with LazyLogging {

  val out: Outlet[String] = Outlet[String]("NaviBlobSource")

  override val shape: SourceShape[String] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            val f: Future[Any] = connector ask Pull()
            Await.result(f, to.duration) match {
              case data: String => push(out, data)
              case _: NoMore =>
                logger.info(
                  "blob stream is finished. all blobs have been read.")
              case e => logger.warn(s"pull error: $e", e)
            }
          }
        }
      )
    }

}
