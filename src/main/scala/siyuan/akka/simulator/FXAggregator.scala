package siyuan.akka.simulator

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class SimulatorActor extends Actor with ActorLogging {


  val fileLoader = context.actorOf(Props[FileLoader], "FileLoader")

  def receive = {

    case "Start" =>
      log info ("Start FX Simulator..")
      fileLoader ! "Start"

  }

}

/*
 * -XX:MaxDirectMemorySize=12g 
 */
object FXAggregator{

  val system = ActorSystem("FXAggregator")
  val actor = system.actorOf(Props[SimulatorActor], "FXAggregatorSimulator")

  def send(msg: String) {
    actor ! msg
  }

  def main(args: Array[String]) {
    send("Start")
  }
}