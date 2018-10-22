package onextent.akka.naviblob.akka

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.scaladsl.Source
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.util.Timeout
import com.sksamuel.avro4s.{Decoder, SchemaFor}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag

/**
  * Convenience entry point api. Users instantiate this and wire it into their streams.
  */
object NaviBlob {

  def apply[T >: Null: Decoder: SchemaFor: ClassTag](connector: ActorRef)(
      implicit system: ActorSystem,
      to: Timeout): Source[T, NotUsed] =
    Source.fromGraph(new NaviBlob[T](connector))

}

final case class Pull()
final case class NoMore()

/**
  * Entry point api. Users instantiate this and wire it into their streams.
  */
class NaviBlob[T >: Null: Decoder: SchemaFor: ClassTag](connector: ActorRef)(
    implicit system: ActorSystem,
    to: Timeout)
    extends GraphStage[SourceShape[T]]
    with LazyLogging {

  val out: Outlet[T] = Outlet[T]("NaviBlobSource")

  override val shape: SourceShape[T] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            val f: Future[Any] = connector ask Pull()
            Await.result(f, to.duration) match {
              case data: T => push(out, data)
              case _: NoMore =>
                logger.info(
                  "blob stream is finished. all blobs have been read.")
                completeStage()
              case e => logger.warn(s"pull error: $e", e)
            }
          }
        }
      )
    }

}
