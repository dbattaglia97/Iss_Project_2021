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
		var position =	""
		var status =	""

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
					clientactor = QakContext.getActor("client2")
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

	@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
 	fun testWorkFlow(){
		runBlocking{
			delay(5000)
			val channelForObserver = Channel<String>()
			
			
			delay(1000)
			
			testingObserver!!.addObserver(channelForObserver, "indoorAtStart")
			testingObserver!!.addObserver(channelForObserver, "outdoorAtStart")
			testingObserver!!.addObserver(channelForObserver, "posRobot")
			testingObserver!!.addObserver(channelForObserver, "slotnum")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			testingObserver!!.addObserver(channelForObserver, "movingTo")
			testingObserver!!.addObserver(channelForObserver, "carslotnum")
			testingObserver!!.addObserver(channelForObserver, "indoorS")
			testingObserver!!.addObserver(channelForObserver, "weight")
			testingObserver!!.addObserver(channelForObserver, "outdoorS")
		
			delay(3000) 
			
			//clientactor!!.forward("startManager","system(ready)","parkingmanagerservice")
		
			delay(5000)
		
		
			var indoorStatus = channelForObserver.receive().substringAfter("(","-").substringBefore(")","-")
			println("TEST indoorAtStart: $indoorStatus")
			assertTrue(indoorStatus.equals("FREE"))
		
			var outdoorStatus = channelForObserver.receive().substringAfter("(","-").substringBefore(")","-")
			println("TEST outdoorStatus: $outdoorStatus")
			assertTrue(outdoorStatus.equals("FREE"))
	
		
			var robotPosition = channelForObserver.receive().substringAfter("(","-").substringBefore(")","-")
			println("TEST robotPosition: $robotPosition")
			assertTrue(robotPosition.equals("0,0"))
		
			delay(3000)
		
			//clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")
		
		
			var slotnum 		= channelForObserver.receive().substringAfter("(","-").substringBefore(")","-").toInt()
			println("TEST slotnum: $slotnum")
			assertTrue(slotnum > 0 && slotnum < 7)
		
			delay(3000)
		
			//clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
		
			var weight	= channelForObserver.receive().substringAfter("(","").substringBefore(")","").toInt()
			println("TEST weight: $weight")
			assertTrue(weight > 0)
			println("TEST weight: SUCCESS")
		
			indoorStatus 		= channelForObserver.receive().substringAfter("(","").substringBefore(")","")
			println("TEST indoorStatus: $indoorStatus")
			assertTrue(indoorStatus.equals("BUSY"))
			println("TEST indoorStatus SUCCESS")
		
			var result = channelForObserver.receive()
			robotPosition = result.substringAfter("(","").substringBefore(")","")
			println("TEST robotPosition: $robotPosition")
			assertTrue(robotPosition.equals("6,0"))
			println("TEST robotPosition SUCCESS")
		
			result = channelForObserver.receive()
			indoorStatus 		= result.substringAfter("(","").substringBefore(")","")
			println("TEST indoorStatus: $indoorStatus")
			assertTrue(indoorStatus.equals("FREE"))
			println("TEST indoorStatus SUCCESS")
		
			var receipt 		= channelForObserver.receive().substringAfter("(","-").substringBefore(")","-").toInt()
			println("TEST receipt: $receipt")
			assertTrue(receipt >= 11)
		
			result = channelForObserver.receive()
			robotPosition = result.substringAfter("(","").substringBefore(")","")
			println("TEST robotPosition: $robotPosition")
			assertTrue(robotPosition.equals("1,1"))
		
		
			delay(2000)
		
			var home 		= channelForObserver.receive().substringAfter("(","-").substringBefore(")","-")
			println("TEST moving to HOME: $home")
			assertTrue(home.equals("HOME"))
			
			println("FINISH PARKING OP")
		
			delay(4000)
		
			//clientactor!!.request("pickup","pickup($receipt)","parkingmanagerservice")
		
			var carslotnum = channelForObserver.receive()
			var carslot = carslotnum.substringAfter("(","-").substringBefore(")","-").toInt()
			println("TEST carslotnum: $carslot")
			assertTrue(carslot.equals(slotnum))
			println("TEST carslotnum SUCCESS")
		
			
			result = channelForObserver.receive()
			println("res= "+result)
			result = channelForObserver.receive()
			println("res2= "+result)
			robotPosition = result.substringAfter("(","").substringBefore(")","")
			println(""+ robotPosition)
			assertTrue(robotPosition.equals("1,1"))
			println("TEST robotPosition 1: $robotPosition")
		
			result = channelForObserver.receive()
			robotPosition = result.substringAfter("(","").substringBefore(")","")
			assertTrue(robotPosition.equals("6,4"))
			println("TEST robotPosition 2: $robotPosition")
		
			
			outdoorStatus = channelForObserver.receive().substringAfter("(","").substringBefore(")","")
			assertTrue(outdoorStatus.equals("BUSY"))
			println("TEST outdoorStatus: $outdoorStatus")
		
			outdoorStatus = channelForObserver.receive().substringAfter("(","").substringBefore(")","")
			assertTrue(outdoorStatus.equals("FREE"))
			println("TEST outdoorStatus: $outdoorStatus")
			
		
			home = channelForObserver.receive().substringAfter("(","-").substringBefore(")","-")
			assertTrue(home.equals("HOME"))
			println("TEST moving to HOME: $home")
		
			println("FINISH PICKING OP")
			
			delay(8000)
		}
		}
		

}
