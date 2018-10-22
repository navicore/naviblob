package onextent.akka.naviblob.akka

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.naviblob.akka.EhCaptureConnector.Pull

import scala.concurrent.duration._
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
            logger.debug("source onPull")
            val f: Future[Any] = connector ask Pull()
            Await.result(f, 120 seconds) match {
              case data: String => push(out, data)
              case e            => logger.warn(s"pull error: $e", e)
            }
          }
        }
      )
    }

}
