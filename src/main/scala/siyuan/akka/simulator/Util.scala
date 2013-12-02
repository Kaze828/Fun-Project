package siyuan.akka.simulator

import java.nio.ByteBuffer


object Util {

  private val ENCODING_CHARSET = "UTF-8"

  /**
   * Decodes a Byte array into a {@code String}, using the UTF-8 encoding.
   */
  def decodeString( byteArray: Array[Byte], start: Int, length: Int ) : String = {
    new String( byteArray, ENCODING_CHARSET )
  }


//  def constructMarketData( byteArray: Array[Byte], offset: Int, length: Int ) : MarketData = {
//
//
//    var data = new MarketData()
//
//    return data
//  }
  
  def printStringArray(array: Array[String]){
    for( element <- array ) print(element + ",")
    println()
  }
}