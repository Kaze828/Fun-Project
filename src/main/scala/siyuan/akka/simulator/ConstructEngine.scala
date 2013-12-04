package siyuan.akka.simulator

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import scala.collection.mutable.ArrayBuffer
import java.nio.ByteBuffer


object ConstructEngine {

  case class Data(marketData: MarketData)

//  case class Line(array: Array[Byte], isLastLine: Boolean)

  case class Line( buffer: ByteBuffer, offset: Int, length: Int, isLastLine: Boolean)

}

class ConstructEngine extends Actor with ActorLogging {

  import ConstructEngine._

  private final val SPLITTER = ','

  val computeEngine = context.actorOf(Props[ComputeEngine], "ComputeEngine")

  def receive = {
    case Line(buffer, offset, length, isLastLine) =>
      val array = new Array[Byte](length)
      buffer.position(offset)
      buffer.get(array)
      val data = constructMarketData(array, isLastLine)
      computeEngine ! Data(data)
    case _ => {}

  }

  /**
   * 15.6255,AutoFx,EURUSD,1.4675,3000000,1.4679,2000000
   * TODO need to be optimized
   * @param byteArray
   * @return
   */
  private def constructMarketData(byteArray: Array[Byte], isLastLine:Boolean): MarketData = {


    val indices =  parseByteArray( byteArray )

    val marketData = new MarketData(
      new String( byteArray.slice(0, indices(0) ) ).toDouble, // timestamp
      new String( byteArray.slice( indices(0)+1, indices(1) ) ), //  market name
      new String( byteArray.slice( indices(1)+1, indices(2) ) ), //  pair name
      new String( byteArray.slice( indices(2)+1, indices(3) ) ).toDouble, // bid price
      new String( byteArray.slice( indices(3)+1, indices(4) ) ).toInt, // bid count
      new String( byteArray.slice( indices(4)+1, indices(5) ) ).toDouble, // ask price
      new String( byteArray.slice( indices(5)+1, byteArray.length) ).toInt, // ask count
      isLastLine
    )
    marketData
  }

  /**
   * parse a byte array and return a list of indices where the splitters locate
   **/
  private def parseByteArray( byteArray: Array[Byte]) :  Array[Int] ={
      var indices = new ArrayBuffer[Int]
      var index = 0;
      for( byte <- byteArray ){
        if( byte == SPLITTER) {
          indices += index
        }
        index = index + 1
      }
      return indices.toArray
  }


}