package siyuan.akka.fun

//implement the following api with the indicated complexities
//This should be an *immutable* queue
trait Queue[T] {
  //O(1)
  def isEmpty: Boolean
  //O(1)
  def insert(t: T): Queue[T]
  //O(1)
  def head: Option[T]
  //O(1) amortised
  def tail: Queue[T]
}

object Queue {
  def apply[T](xs: T*): Queue[T] = new QueueImpl[T](xs.toList, Nil)
  def empty[T]: Queue[T] = EmptyQueue.asInstanceOf[Queue[T]]

  private class QueueImpl[T] (in_list: List[T], out_list: List[T]) extends Queue[T] {
    def isEmpty: Boolean = in_list.isEmpty && out_list.isEmpty

    def head: Option[T] = {
      if( out_list.nonEmpty) Some( out_list.head )
      else if( in_list.nonEmpty) Some( in_list.last )
      else None
    }

    def tail: QueueImpl[T] = {
      if( out_list.nonEmpty ) new QueueImpl[T]( in_list, out_list.tail)
      else if( in_list.nonEmpty ) new QueueImpl( Nil, in_list.reverse.tail )
      else throw new NoSuchElementException( "tail on empty queue" )
    }

    def insert( x: T) = new QueueImpl[T]( x :: in_list, out_list )
    override def toString() = "in_list: " + in_list.toString() + " out_list: " + out_list.toString()
  }

  private object EmptyQueue extends QueueImpl[Nothing](Nil, Nil) {}
}