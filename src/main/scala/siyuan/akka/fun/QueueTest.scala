package siyuan.akka.fun

/**
 * Created with IntelliJ IDEA.
 * User: siyuanhe
 * Date: 13-11-29
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
object QueueTest {
   def main( args: Array[String]) {
     val queue = Queue.empty[Int]
     val new_q = queue.insert(4)
     println("queue is "+ new_q)
     println("Head is " + new_q.head)
     println("Tail is " + new_q.tail)

     val new_q_2 = new_q.insert(3)
     println("queue is "+ new_q_2)
     println("Head is " + new_q_2.head)

     val new_q_3 = new_q_2.insert(2)
     println("queue is "+ new_q_3)
     println("Head is " + new_q_3.head)

     val new_q_4 = new_q_3.tail
     println("queue is "+ new_q_4)
     println("Head is " + new_q_4.head)
   }
}
