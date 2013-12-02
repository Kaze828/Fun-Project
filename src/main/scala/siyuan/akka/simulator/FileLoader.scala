package siyuan.akka.simulator

import java.io.{FileInputStream, File}
import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.{ListBuffer}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


class FileLoader extends Actor with ActorLogging {

  import ConstructEngine._


  var buffer: ByteBuffer = null

  val fileName = ConfigFactory.load().getString("file_to_process")
  val construct_engine = context.actorOf(Props[ConstructEngine], "Construct_Engine")

  /**
   * TODO
   * need to test in different environment.
   * Line separator might be different.
   */
  private final val LINESEPARATOR : Byte = '\n'.toByte

  private def skipTheFirstLine(){
    while( buffer.hasRemaining && buffer.get != LINESEPARATOR ){}
  }

  def receive = {
    case "Start" =>
      loadData
     // send each line to
      var list = new ListBuffer[Byte]()
      while( buffer.hasRemaining ){

        val temp = buffer.get
        if( temp == LINESEPARATOR ){
          construct_engine ! Line( list.toArray, false )
          list.clear()
        }else{
          list += temp
        }

      }

      //send last line
      construct_engine ! Line( list.toArray, true )
    case _ =>
      log info ("other message")

  }

  private def loadData = {

    val file = new File(fileName)
    val fileSize = file.length()
    val stream = new FileInputStream(file)

    /*
     * TODO
     * need to read from file end.
     * the limitation of buffer is 2G since map function only takes integer.
     */
    buffer = stream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileSize)
    stream.close()

    //skip the first line, they are just column names.
    skipTheFirstLine
  }

  override def preStart() {

  }
}