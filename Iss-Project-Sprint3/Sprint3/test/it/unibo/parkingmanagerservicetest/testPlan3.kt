import alice.tuprolog.*
import org.junit.Assert.*
import java.net.UnknownHostException
import org.junit.BeforeClass
import cli.System.IO.IOException
import org.junit.Test
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import it.unibo.kactor.QakContext
import org.junit.Before
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import org.junit.AfterClass
import it.unibo.kactor.sysUtil
import it.unibo.kactor.ApplMessage
import org.junit.After
import kotlinx.coroutines.Job
import itunibo.planner.plannerUtil


class TestPlan {

		companion object tester {

		var testingObserver   : CoapObserverForTesting ? = null
		var systemStarted         = false
		val channelSyncStart      = Channel<String>()
		var myactor           : ActorBasic? = null
		var clientactor           : ActorBasic? = null
		var counter               = 1

		var token	=	0
		var slotnum =	0

		@JvmStatic
        @BeforeClass
		//@Target([AnnotationTarget.FUNCTION]) annotation class BeforeClass
		//@Throws(InterruptedException::class, UnknownHostException::class, IOException::class)

		fun init() {

			GlobalScope.launch{
				it.unibo.ctxcarparking.main()

			}
			GlobalScope.launch{
				myactor = QakContext.getActor("parkingmanagerservice")
				clientactor = QakContext.getActor("client")
 				while(  myactor == null )		{
					println("+++++++++ waiting for system startup ...")
					delay(500)
					myactor=QakContext.getActor("parkingmanagerservice")
					clientactor = QakContext.getActor("client")
				}
				channelSyncStart.send("starttesting")
			}

		}//init

		@JvmStatic
	    @AfterClass
		fun terminate() {
			println("terminate the testing")
		}

	}//object



	@Before
	fun checkSystemStarted()  {
		testingObserver = CoapObserverForTesting("testingobs","ctxcarparking","testupdater","5683")
		println("\n=================================================================== ")
	    println("+++++++++ BEFOREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE testingObserver=$testingObserver")
		if( ! systemStarted ) {
			runBlocking{
				channelSyncStart.receive()
				systemStarted = true
			    println("+++++++++ checkSystemStarted resumed ")

			}
		}
		if( testingObserver == null)
			testingObserver = CoapObserverForTesting("obstesting${counter++}")
  	}


	@After
	fun removeObs(){
		println("+++++++++ AFTERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR  ${testingObserver!!.name}")
		testingObserver!!.terminate()
		testingObserver = null
		runBlocking{
			delay(2000)
		}
 	}


//----------------------------------------------------------------------------------------------------------------------------

	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testParkingmanager(){

		runBlocking{
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "temperature")

			var result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			var t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(20, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(25, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			 t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(30, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			 t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(35, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(40, t)


			testingObserver!!.addObserver(channelForObserver, "fan")
			result = channelForObserver.receive()
			println("+++++++++ fan should be ON RESULT=$result +++++++++")
			var fanstatus = result.substringAfter("(",result).substringBefore(")",result)
			assertEquals("ON",fanstatus)


			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			 t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(35, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			 t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(30, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 40 RESULT=$result +++++++++")
			 t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(25, t)

			result = channelForObserver.receive()
			println("+++++++++ temperature 20 RESULT=$result +++++++++")
			t = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertEquals(20, t)


			result = channelForObserver.receive()
			println("+++++++++ fan should be OFF RESULT=$result +++++++++")
			fanstatus = result.substringAfter("(",result).substringBefore(")",result)
			assertEquals("OFF",fanstatus)
		}
	}
}
