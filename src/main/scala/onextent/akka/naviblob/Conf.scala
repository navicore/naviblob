package onextent.akka.naviblob

import akka.Done
import akka.actor.ActorSystem
import akka.pattern.AskTimeoutException
import akka.serialization.SerializationExtension
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Conf extends LazyLogging {

  val conf: Config = ConfigFactory.load()
  implicit val actorSystem: ActorSystem =
    ActorSystem(conf.getString("main.appName"), conf)
  SerializationExtension(actorSystem)

  val decider: Supervision.Decider = {

    case e: AskTimeoutException =>
      logger.warn(s"decider restarting due to " + e)
      Supervision.Restart

    case e: java.text.ParseException =>
      logger.warn(
        s"decider discarding unparseable message to resume processing: $e")
      Supervision.Resume

    case e: java.util.concurrent.TimeoutException =>
      logger.warn(s"decider restarting due to " + e)
      Supervision.Restart
    case e =>
      logger.error(s"decider can not decide: $e")
      Supervision.Restart

  }

  implicit val materializer: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider))

  def handleTerminate(result: Future[Done]): Unit = {
    result onComplete {
      case Success(_) =>
        logger.warn("success. but stream should not end!")
        actorSystem.terminate()
      case Failure(e) =>
        logger.error(s"failure. stream should not end! $e", e)
        actorSystem.terminate()
    }
  }

}
