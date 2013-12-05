package siyuan.akka.simulator

import akka.actor.{Props, ActorLogging, Actor}

import scala.collection.immutable.HashMap


class ComputeEngine extends Actor with ActorLogging {

  import LogPrinter._
  import ConstructEngine._

  val logPrinter = context.actorOf(Props[LogPrinter], "LogPrinter")
  //  @inline def add_two_element(e1: Int, e2: Int): Int = e1 + e2


  var previous_bid_quotes = new HashMap[String, Pair[Double, Int]]()
  var previous_ask_quotes = new HashMap[String, Pair[Double, Int]]()


  var previous_time = 0;


  def receive = {

    case Data(marketData: MarketData) =>
      tellLogPrinter(marketData)

      updatePreviousQuotes(marketData)

  }


  private def tellLogPrinter(marketData: MarketData) {

    // tell log printer to print message since another 100 millsecs passed
    val current_time = marketData.timeStamp.toInt / 100
    if (current_time > previous_time) {
      logPrinter ! LogMessage( current_time, false, previous_bid_quotes, previous_ask_quotes)
      previous_time = current_time
    }

    // info log printer it's no more messages
    if (marketData.isLastData == true) {
      logPrinter ! LogMessage(current_time, true, previous_bid_quotes, previous_ask_quotes)
    }

  }

  private def updatePreviousQuotes(marketData: MarketData) {
    if (marketData.bid != 0) {
      previous_bid_quotes = previous_bid_quotes.updated((marketData.marketName + marketData.cPair), (marketData.bid, marketData.bidSize))
    }
    if (marketData.ask != 0) {
      previous_ask_quotes = previous_ask_quotes.updated((marketData.marketName + marketData.cPair), (marketData.ask, marketData.askSize))
    }
  }


}