package siyuan.akka.simulator

/**
 * Created with IntelliJ IDEA.
 * User: siyuanhe
 * Date: 13-11-30
 * Time: 11:55 AM
 *
 */
class MarketData( _timeStamp : Double,
                  _marketName : String,
                  _cPair: String,
                  _bid: Double,
                  _bidSize: Int,
                  _ask: Double,
                  _askSize: Int,
                  _isLastData: Boolean) {


  def timeStamp = _timeStamp
  def marketName = _marketName
  def cPair = _cPair
  def bid = _bid
  def bidSize = _bidSize
  def ask = _ask
  def askSize = _askSize
  def isLastData = _isLastData

}
