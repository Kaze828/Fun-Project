package siyuan.akka.simulator

import akka.actor.{Props, ActorLogging, Actor}
import java.util.HashMap
import scala.collection.immutable.TreeMap


class ComputeEngine extends Actor with ActorLogging {

  import LogPrinter._
  import ConstructEngine._

  val logPrinter = context.actorOf(Props[LogPrinter], "LogPrinter")
  //  @inline def add_two_element(e1: Int, e2: Int): Int = e1 + e2

  val CURRENCY_PAIRS = Array(
    "EURUSD",
    "GBPUSD",
    "EURGBP",
    "USDCHF",
    "EURCHF",
    "USDJPY",
    "EURJPY",
    "AUDUSD",
    "NZDUSD",
    "USDCAD"
  )

  val MARKETS = Array(
    "AutoFx",
    "EasyFx",
    "FxPrime",
    "eCurrencies",
    "FxMart"
  )

  var bid_map = new HashMap[String, TreeMap[Double, Int]]()
  var ask_map = new HashMap[String, TreeMap[Double, Int]]()
  for (pair <- CURRENCY_PAIRS) {
    bid_map.put(pair, TreeMap.empty[Double, Int])
    ask_map.put(pair, TreeMap.empty[Double, Int])
  }

  var previous_bid_quotes = new HashMap[String, Pair[Double, Int]]()
  var previous_ask_quotes = new HashMap[String, Pair[Double, Int]]()


  var previous_time = 0;


  def receive = {

    case Data(marketData: MarketData) =>

      // handle bid first
      if (marketData.bid != 0) {
        removePreviousBidFromResult(marketData)
        updateBidResult(marketData)
      }

      if (marketData.ask != 0) {
        removePreviousAskFromResult(marketData)
        updateAskResult(marketData)
      }
      // update quotes history per market/pairs
      updatePreviousQuotes(marketData)
      tellLogPrinter(marketData)
  }

  private def removePreviousBidFromResult(marketData: MarketData) {
    if (previous_bid_quotes.containsKey(marketData.marketName + marketData.cPair)) {
      val previous_bid_quote = previous_bid_quotes.get(marketData.marketName + marketData.cPair)

      // remove previous quote from the aggregated result
      // update bid info

      val bid_in_prequote = previous_bid_quote._1
      val bidSize_in_prequote = previous_bid_quote._2
      val bid_rates = bid_map.get(marketData.cPair)
      val bid_size = bid_rates.get(bid_in_prequote)


      bid_size match {
        case Some(size) => {
          // if it's a new quote, add to current rate
          val newSize = size - bidSize_in_prequote
          if (newSize > 0) {
            bid_map.put(marketData.cPair, bid_rates.updated(bid_in_prequote, newSize))
          } else {
            bid_map.put(marketData.cPair, bid_rates - bid_in_prequote)
          }

        }
        case None => {}
      }

    }
  }

  private def removePreviousAskFromResult(marketData: MarketData) {

    if (previous_ask_quotes.containsKey(marketData.marketName + marketData.cPair)) {
      val previous_ask_quote = previous_ask_quotes.get(marketData.marketName + marketData.cPair)


      val ask_in_prequote = previous_ask_quote._1
      val askSize_in_prequote = previous_ask_quote._2
      val ask_rates = ask_map.get(marketData.cPair)
      val ask_size = ask_rates.get(ask_in_prequote)


      ask_size match {
        case Some(size) => {
          // if it's a new quote, add to current rate
          val newSize = size - askSize_in_prequote
          if (newSize > 0) {
            ask_map.put(marketData.cPair, ask_rates.updated(ask_in_prequote, newSize))
          } else {
            ask_map.put(marketData.cPair, ask_rates - ask_in_prequote)
          }

        }
        case None => {}
      }
    }


  }

  private def updateBidResult(marketData: MarketData) {

    // update bid info
    val bid_rates = bid_map.get(marketData.cPair)
    val bid_size = bid_rates.get(marketData.bid)


    bid_size match {
      case Some(size) => {
        // if it's a new quote, add to current rate
        bid_map.put(marketData.cPair, bid_rates.updated(marketData.bid, size + marketData.bidSize))
      }

      // only will be executed when a new bid appears for a currency at the first time
      case None => {
        bid_map.put(marketData.cPair, bid_rates.updated(marketData.bid, marketData.bidSize))
      }
    }
  }

  private def updateAskResult(marketData: MarketData) {
    // update ask info
    val ask_rates = ask_map.get(marketData.cPair)
    val ask_size = ask_rates.get(marketData.ask)
    ask_size match {
      case Some(size) => {
        ask_map.put(marketData.cPair, ask_rates.updated(marketData.ask, size + marketData.askSize))

      }
      // only will be executed when a new bid appears for a currency  at the first time
      case None => {
        ask_map.put(marketData.cPair, ask_rates.updated(marketData.ask, marketData.askSize))
      }
    }
  }

  private def tellLogPrinter(marketData: MarketData) {

    // tell log printer to print message since another 100 millsecs passed
    val current_time = marketData.timeStamp.toInt / 100
    if (current_time > previous_time) {
      logPrinter ! LogMessage(bid_map, ask_map, current_time, false)
      previous_time = current_time
    }

    // info log printer it's no more messages
    if (marketData.isLastData == true) {
      logPrinter ! LogMessage(bid_map, ask_map, current_time, true)
    }

  }

  private def updatePreviousQuotes(marketData: MarketData) {
    if (marketData.bid != 0) {
      previous_bid_quotes.put((marketData.marketName + marketData.cPair), (marketData.bid, marketData.bidSize))
    }
    if (marketData.ask != 0) {
      previous_ask_quotes.put((marketData.marketName + marketData.cPair), (marketData.ask, marketData.askSize))
    }
  }


}