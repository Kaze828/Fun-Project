package siyuan.akka.simulator

import java.io.{FileInputStream, File}
import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.{ListBuffer}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import scala.util.control._

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



  def receive = {
    case "Start" =>
      loadData
     // send each line to
      var start = 0;
      var end = 0;

      // skip the first line
      while( buffer.hasRemaining && buffer.get != LINESEPARATOR ){
       end = end + 1
      }
      end = end + 1;
      start = end

      //start sending data
      while( buffer.hasRemaining ){
        end = end + 1
        val temp = buffer.get
        if( temp == LINESEPARATOR ){
          construct_engine ! Line( buffer.duplicate, start, end-start-1, false )
          start = end 
        }
      }

      //send last line
      construct_engine ! Line( buffer, start, end-start, true )
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
//    skipTheFirstLine
  }

  override def preStart() {

  }
}