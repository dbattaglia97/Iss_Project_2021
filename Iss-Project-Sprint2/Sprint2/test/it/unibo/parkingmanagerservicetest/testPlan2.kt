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
				it.unibo.ctxCarParking.main()

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
				myactor!!.solve("consult('../../../../parkingAreaKb.pl')","")
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
		testingObserver 	= CoapObserverForTesting("testingobs","ctxcarparking","testupdater","5693")
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

/*========================
 -	Type: Integration Test
 -  COMPLETE WORKFLOW
===========================*/

	@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun completeWorkflow(){

		runBlocking{
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "slotnum")
			delay(3000)

			clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")

			println("+++++++++ testreqenter ")
			var result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			slotnum = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum > 0)

			delay(2000)

			//--------------------------------------------------------------------------------

			testingObserver!!.addObserver(channelForObserver, "position")
			println("+++++++++ testMoveToIn ")
			result = channelForObserver.receive()
			println("+++++++++ testMoveToIn RESULT=$result +++++++++")
			var position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			println("+++++++++ testMoveToIn POSITION=$position +++++++++")
			assertEquals(position, "6,0")

			//--------------------------------------------------------------------------------

			testingObserver!!.addObserver(channelForObserver, "weight")
			clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )


			//testingObserver!!.addObserver(channelForObserver, "position")
			println("+++++++++ testMoveToSlotIn ")
			result = channelForObserver.receive()
			println("+++++++++ testMoveToSlotIn RESULT=$result +++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals(position, "1,1")


			println("+++++++++ testcarenter")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			result = channelForObserver.receive()
			token = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token > 10)

			delay(4000)
			//----------------------------------------------------------------------------------


			testingObserver!!.addObserver(channelForObserver!!, "pickup")
			clientactor!!.request("pickup","pickup($token)","parkingmanagerservice")

			println("+++++++++ testpickup")
			result = channelForObserver.receive()
			println("+++++++++ testpickup RESULT=$result +++++++++")
			assertTrue(result.substringAfter("(",result).substringBefore(")",result).toInt() > 0)


			//testingObserver!!.addObserver(channelForObserver, "position")
			println("+++++++++ testMoveToSlotIn ")
			result = channelForObserver.receive()
			println("+++++++++ testMoveToSlotIn RESULT=$result +++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals(position, "1,1")


			//testingObserver!!.addObserver(channelForObserver, "position")
			println("+++++++++ testMoveToOutdoor ")
			result = channelForObserver.receive()
			println("+++++++++ testMoveToSlotIn RESULT=$result +++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals(position, "6,4")

			delay(1000)
			//-----------------------------------------------------------------------------------
 			testingObserver!!.addObserver(channelForObserver, "outdoorStatus")

			println("+++++++++ testoutsonar Occupied")
			resultSensor = channelForObserver.receive()
			print(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString())
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "BUSY" )
			println("+++++++++ testoutsonar Occupied RESULT=$resultSensor +++++++++")

			clientactor!!.emit("carwithdrawn", "cw(bye)" )

			println("+++++++++ testoutsonar Freed")
			resultSensor = channelForObserver.receive()
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "FREE" )
			println("+++++++++ testoutsonar Freed RESULT=$resultSensor +++++++++")
	  	}
 	}



/*========================
 -	Type: Unit Test
 -  DIRECTION TEST
===========================*/

	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun directionTest(){

		runBlocking{
			val channelForObserver = Channel<String>()

			testingObserver!!.addObserver(channelForObserver, "position")

			myactor!!.forward("trolleycmd","trolleycmd(moveToIn)","trolley")
			var result = channelForObserver.receive()
			var direction = plannerUtil.getDirection()
			println("+++++++++ trolleyInIndoor RESULT=$result - DIRECTION=$direction+++++++++")
			var position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals("6,0", position)
			assertEquals("downDir", plannerUtil.getDirection())


			delay(4000)
			//----------------------------------------------------------------------------------

			myactor!!.forward("trolleycmd","trolleycmd(moveToSlot1)","trolley")


			result = channelForObserver.receive()
			direction = plannerUtil.getDirection()
			println("+++++++++ trolleyInP1 RESULT=$result - DIRECTION=$direction+++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals("1,1", position)
			assertEquals("leftDir", plannerUtil.getDirection())


			delay(4000)
			//----------------------------------------------------------------------------------

			myactor!!.forward("trolleycmd","trolleycmd(moveToOut)","trolley")


			result = channelForObserver.receive()
			direction = plannerUtil.getDirection()
			println("+++++++++ trolleyInOutdoor RESULT=$result - DIRECTION=$direction+++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals("6,4", position)
			assertEquals("upDir", plannerUtil.getDirection())


			delay(4000)
			//----------------------------------------------------------------------------------

			myactor!!.forward("trolleycmd","trolleycmd(moveToSlot2)","trolley")

			result = channelForObserver.receive()
			direction = plannerUtil.getDirection()
			println("+++++++++ trolleyInP2 RESULT=$result - DIRECTION=$direction+++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals("4,1", position)
			assertEquals("rightDir", plannerUtil.getDirection())

			delay(4000)
			//----------------------------------------------------------------------------------

			myactor!!.forward("trolleycmd","trolleycmd(moveToHome)","trolley")

			result = channelForObserver.receive()
			direction = plannerUtil.getDirection()
			println("+++++++++ trolleyInHome RESULT=$result - DIRECTION=$direction+++++++++")
			position = result.substringAfterLast("(",result).substringBefore(")",result).toString()
			assertEquals("0,0", position)
			assertEquals("downDir", plannerUtil.getDirection())
	  	}
	}

}
