package siyuan.akka.simulator

import akka.actor.{ActorLogging, Actor}
import java.util.HashMap
import scala.collection.immutable.TreeMap
import com.typesafe.config.ConfigFactory
import java.io.PrintWriter
import java.lang.System
import java.text.SimpleDateFormat
import scala.Predef._

object LogPrinter{
  case class LogMessage( time: Int,
                         noMoreMessage: Boolean,
                         bid_quotes:  scala.collection.immutable.HashMap[String, Pair[Double, Int]],
                         ask_quotes:  scala.collection.immutable.HashMap[String, Pair[Double, Int]])
}

/**
 * Created with IntelliJ IDEA.
 * User: siyuanhe
 * Date: 13-12-01
 */
class LogPrinter extends Actor with ActorLogging {
  import LogPrinter._

  val num_of_quotes_to_print = ConfigFactory.load().getInt("num_of_quotes_to_print")
  val log_file_name = ConfigFactory.load().getString("log_file_name")
  val start_time_stamp = System.currentTimeMillis()

  //11/03/2008 11:24:27,100 11/03/2008 11:24:27,100
  val sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss, S");
  var buffer = new StringBuilder

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


  var bid_map = new HashMap[String, TreeMap[Double, Int]]()
  var ask_map = new HashMap[String, TreeMap[Double, Int]]()
  for (pair <- CURRENCY_PAIRS) {
    bid_map.put(pair, TreeMap.empty[Double, Int])
    ask_map.put(pair, TreeMap.empty[Double, Int])
  }

  var out = new PrintWriter( log_file_name )

  def receive = {
    case LogMessage( time, noMoreMessage, bid_quotes, ask_quotes ) =>  {
         if( ! noMoreMessage ){
           aggregateResult(bid_map, bid_quotes)
           aggregateResult(ask_map, ask_quotes)
           printQuote( bid_map, ask_map, time)
         }else{
           // close the stream
           out.close();
           log info "No More Messages, Shutting Down Simulator"
           context.system.shutdown()
         }

    }



  }

  private def aggregateResult( map: HashMap[String, TreeMap[Double, Int]],
                               quotes: scala.collection.immutable.HashMap[String, Pair[Double, Int]]) = {
    for (pair <- CURRENCY_PAIRS) {
      map.put(pair, TreeMap.empty[Double, Int])
    }


    val it = quotes.iterator

    while( it.hasNext ){
      val element = it.next()
      val key = element._1 // key is "marketname" + currencypair
      val currency_pair = key.substring(key.length-6, key.length )

      val value_pair = element._2        // contains(rate, rate_size)
      val rate = value_pair._1
      val rate_size = value_pair._2

      val rates = map.get(currency_pair )

      val old_size = rates.get( rate )

      old_size match {
        case Some(size) => {
          map.put(currency_pair, rates.updated(rate,  size + rate_size))

        }
        // only will be executed when a new bid appears for a currency  at the first time
        case None => {
          map.put(currency_pair, rates.updated(rate, rate_size))
        }
      }

    }

  }


  /**
   * 11/03/2008 11:24:27,100 GBPUSD - Bid: 10 @ 1.6831, 5 @ 1.6832, 30 @ 1.6835 / Ask: 27 @ 1.6837, 19 @ 1.6838, 4 @ 1.6841
   *
   */
  private def printQuote( bid_map: HashMap[String, TreeMap[Double, Int]], ask_map: HashMap[String, TreeMap[Double, Int]], time: Int) = {
    buffer.clear()

    val current_time = sdf.format( start_time_stamp + time*100)


    val it = bid_map.keySet().iterator()
    while (it.hasNext) {
      val currency_pair = it.next()
      buffer.append( current_time + " " + currency_pair + " - ")

      // add bid quotes
      buffer.append( "Bid: ")
      appendQuotes( bid_map, currency_pair)

      // add ask quotes
      buffer.append( " / Ask: ")
      appendQuotes( ask_map, currency_pair)
      buffer.append("\n")
    }

    out.print( buffer.toString() )
    out.flush()
  }

  private def appendQuotes( map: HashMap[String, TreeMap[Double, Int]], currency_pair: String)   {
    val rates = map.get(currency_pair)

    var count = 0
    rates.take(num_of_quotes_to_print).foreach({
      case (key, value) => {
        count = count + 1
        if( count < rates.size && count < num_of_quotes_to_print ) {
          buffer.append( value + " @ " + key + ", ")
        } else{
          buffer.append( value + " @ " + key)
        }

      }
    })

  }


}
