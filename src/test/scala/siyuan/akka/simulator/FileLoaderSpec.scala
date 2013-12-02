package siyuan.akka.simulator

import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.TestActorRef
import org.scalatest.fixture.WordSpec
import akka.actor.ActorSystem



//class FileLoaderSpec extends TestKit( ActorSystem("FileLoaderSpec"))
//							with ImplicitSender
//							with WordSpec
//							with MustMatchers
//							with BeforeAndAfterAll{
//	import ComputeEngine._
//	
//	override def afterAll() { system.shutdown }
//	
//	class Helper{
//	  
//	  def actor() = {
//	    val a = TestActorRef[FileLoader]
//	    (a, a.underlyingActor)
//	  }
//	}
//	
//	"FileLoader" should {
//    "be able to give line to compute engine" in {
//      val loader = TestActorRef[FileLoader].underlyingActor
//      loader.receive( GiveMeALine )
//
//    }
//
//  }
//}