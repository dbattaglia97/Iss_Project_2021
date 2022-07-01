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
 -	A FREE SLOT	: 	YES
 -	INDOOR		:	FREE
 -	OUTDOOR		:	FREE
===========================*/
 	@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testWorkflow(){
 		myactor!!.solve("occupySlot(1)","")
 		myactor!!.solve("occupySlot(2)","")
 		myactor!!.solve("occupySlot(3)","")
 		myactor!!.solve("occupySlot(4)","")
 		myactor!!.solve("vacateSlot(5)","")
 		myactor!!.solve("occupySlot(6)","")
 		myactor!!.solve("freedindoor","")
 		myactor!!.solve("freedoutdoor","")

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
			testingObserver!!.addObserver(channelForObserver, "weight")
			clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")

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
			//-----------------------------------------------------------------------------------
 			testingObserver!!.addObserver(channelForObserver, "outdoorStatus")

			println("+++++++++ testoutsonar Occupied")
			resultSensor = channelForObserver.receive()
			print(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString())
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "BUSY" )
			println("+++++++++ testoutsonar RESULT=$resultSensor +++++++++")

			clientactor!!.emit("carwithdrawn", "cw(bye)" )

			println("+++++++++ testoutsonar Freed")
			resultSensor = channelForObserver.receive()
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "FREE" )
			println("+++++++++ testoutr RESULT=$resultSensor +++++++++")
	  	}
 	}



	/*========================
 -	A FREE SLOT	: 	YES
 -	INDOOR		:	NOT FREE  --> FREE
 -	OUTDOOR		:	NOT FREE  --> FREE
=================================*/
 	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testWorkflow2(){
 		myactor!!.solve("occupySlot(1)","")
 		myactor!!.solve("occupySlot(2)","")
 		myactor!!.solve("occupySlot(3)","")
 		myactor!!.solve("occupySlot(4)","")
 		myactor!!.solve("vacateSlot(5)","")
 		myactor!!.solve("occupySlot(6)","")

 		myactor!!.solve("occupiedindoor","")
 		myactor!!.solve("occupiedoutdoor","")

		runBlocking{
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "wait")
			clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")
			
			println("+++++++++ testreqenter ")
			var result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			slotnum = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum > 0)
			myactor!!.solve("occupySlot(5)","")
			myactor!!.solve("freedindoor","")

			delay(3000)

			//--------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "weight")
			clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")

			println("+++++++++ testcarenter")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			result = channelForObserver.receive()
			token = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token > 10)
			delay(3000)
			//----------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "pickup")
			clientactor!!.request("pickup","pickup($token)","parkingmanagerservice")
			delay(3000)
			myactor!!.solve("freedoutdoor","")

			println("+++++++++ testpickup")
			result = channelForObserver.receive()
			println("+++++++++ testpickup RESULT=$result +++++++++")
			assertTrue(result.substringAfter("(",result).substringBefore(")",result).toInt() > 0)
			delay(2000)
		}
	}

//-----------------------------------------------------------------------------------

/*========================
 -	A FREE SLOT	: 	NO FREE SLOT
 -	INDOOR		:	NOT FREE
 -	OUTDOOR		:	FREE
===========================*/
 	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testWorkflow3(){
		myactor!!.solve("consult('../../../../parkingAreaKb.pl')","")
 		myactor!!.solve("occupySlot(1)","")
 		myactor!!.solve("occupySlot(2)","")
 		myactor!!.solve("occupySlot(3)","")
 		myactor!!.solve("occupySlot(4)","")
 		myactor!!.solve("occupySlot(5)","")
 		myactor!!.solve("occupySlot(6)","")
 		myactor!!.solve("occupiedindoor","")
 		myactor!!.solve("freedoutdoor","")

		runBlocking{
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "toHome")
			clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")

			println("+++++++++ testNotFreeSlot ")
			var result = channelForObserver.receive()
			println("+++++++++ testNotFreeSlot RESULT=$result +++++++++")
			assertEquals(result, "toHome(V)")

			myactor!!.solve("vacateSlot(4)","")
			println("One free slot")
			//--------------------------------------------------------------------------------
			delay(1000)
			println("+++++++++ testreqenter ")
			testingObserver!!.addObserver(channelForObserver, "wait")
			clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")

			result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			slotnum = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum > 0)

			myactor!!.solve("freedindoor","")
			println("Free indoor")
			delay(1000)
			//--------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "weight")
			clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
			//--------------------------------------------------------------------------------
			println("+++++++++ testcarenter")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			result = channelForObserver.receive()
			token = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token > 10)
			delay(4000)
			//---------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "pickup")
			clientactor!!.request("pickup","pickup($token)","parkingmanagerservice")
			delay(500)
			println("+++++++++ testpickup")
			result = channelForObserver.receive()
			println("+++++++++ testpickup RESULT=$result +++++++++")
			assertTrue(result.substringAfter("(",result).substringBefore(")",result).toInt() > 0)
			}
 	}



//-----------------------------------------------------------------------------------



/*========================
 -	A FREE SLOT	: 	ONE FREE SLOT
 -	INDOOR		:	FREE
 -	OUTDOOR		:	FREE
 -	TICKET		: 	NOT VALID
===========================*/
	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testWorkflow4(){
 		myactor!!.solve("occupySlot(1)","")
 		myactor!!.solve("occupySlot(2)","")
 		myactor!!.solve("occupySlot(3)","")
 		myactor!!.solve("occupySlot(4)","")
 		myactor!!.solve("vacateSlot(5)","")
 		myactor!!.solve("occupySlot(4)","")
 		myactor!!.solve("freedindoor","")
 		myactor!!.solve("freedoutdoor","")

		runBlocking{

			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "slotnum")
			clientactor!!.request("reqenter","reqenter(bob)","parkingmanagerservice")
			println("+++++++++ testreqenter ")
			var result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			slotnum = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum > 0)
			delay(2000)
			//--------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "weight")
			clientactor!!.request("carenter","carenter(ok)","parkingmanagerservice")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
			//--------------------------------------------------------------------------------
			println("+++++++++ testcarenter")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			result = channelForObserver.receive()
			token = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token > 10)
			delay(2000)
			//----------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "pickup")
			clientactor!!.request("pickup","pickup(12345)","parkingmanagerservice")
			delay(500)
			println("+++++++++ testpickupError")
			result = channelForObserver.receive()
			println("+++++++++ testpickupError RESULT=$result +++++++++")
			assertTrue(result.substringAfter("(",result).substringBefore(")",result).toInt() < 0)
			//-----------------------------------------------------------------------------------
			delay(1000)
			clientactor!!.request("pickup","pickup($token)","parkingmanagerservice")
			delay(500)
			println("+++++++++ testpickupCorrect")
			result = channelForObserver.receive()
			println("+++++++++ testpickupCorrect RESULT=$result +++++++++")
			assertTrue(result.substringAfter("(",result).substringBefore(")",result).toInt() > 0)
	  	}
 	}

//-----------------------------------------------------------------------------------------

/*================
Simultaneous arrival of more clients
- ALL SLOTS FREE
- INDOOR  initially FREE
- OUTDOOR initially FREE
==================*/

	//@Test
 	@kotlinx.coroutines.ObsoleteCoroutinesApi
	fun testWorkflow5(){

 		myactor!!.solve("vacateSlot(1)","")
 		myactor!!.solve("vacateSlot(2)","")
 		myactor!!.solve("vacateSlot(3)","")
 		myactor!!.solve("vacateSlot(4)","")
 		myactor!!.solve("vacateSlot(5)","")
 		myactor!!.solve("vacateSlot(4)","")
 		myactor!!.solve("freedindoor","")
 		myactor!!.solve("freedoutdoor","")

		runBlocking{
			val channelForObserver = Channel<String>()
			testingObserver!!.addObserver(channelForObserver, "slotnum")
			var msg = MsgUtil.buildRequest("client1", "reqenter","reqenter(bob)","parkingmanagerservice")
			MsgUtil.sendMsg(msg,myactor!!)
			msg = MsgUtil.buildRequest("client2", "reqenter","reqenter(george)","parkingmanagerservice")
			MsgUtil.sendMsg(msg,myactor!!)

			println("+++++++++ testreqenter1 ")
			var result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			slotnum = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum > 0)
			delay(2000)
			//--------------------------------------------------------------------------------
			testingObserver!!.addObserver(channelForObserver, "weight")
			msg = MsgUtil.buildRequest("client1", "carenter","carenter(bob)","parkingmanagerservice")
			MsgUtil.sendMsg(msg,myactor!!)
			println("+++++++++ testweightsensor")
			var resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
			println("+++++++++ testcarenter1")
			testingObserver!!.addObserver(channelForObserver, "receipt")
			result = channelForObserver.receive()
			token = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token > 10)
			delay(1000)

			println("+++++++++ testreqenter2")
			result = channelForObserver.receive()
			println("+++++++++ testreqenter RESULT=$result +++++++++")
			var slotnum2 = result.substringAfter("(",result).substringBefore(")",result).toInt()
			assertTrue(slotnum2 > 0 && !slotnum2.equals(slotnum))
			delay(2000)
			//--------------------------------------------------------------------------------

			testingObserver!!.addObserver(channelForObserver, "receipt")
			msg = MsgUtil.buildRequest("client2", "carenter","carenter(george)","parkingmanagerservice")
			MsgUtil.sendMsg(msg,myactor!!)
			println("+++++++++ testweightsensor")
			resultSensor = channelForObserver.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")

			println("+++++++++ testcarenter2")
			result = channelForObserver.receive()
			var token2 = result.substringAfter("(",result).substringBefore(")",result).toInt()
			println("+++++++++ testcarenter RESULT=$result +++++++++")
			assertTrue(token2 > 10 && !token2.equals(token))
	  	}
 	}

//-----------------------------------------------------------------------------------



//-----------------------------------------------------------------------------------------

/*===========================

 Unit Testing: WEIGHTSENSOR, OUTSONAR
============================*/

//@Test
	fun testWeightsensor(){
		runBlocking{
			val channelForUnitTesting = Channel<String>()
			testingObserver!!.addObserver(channelForUnitTesting, "weight")
			clientactor!!.emit("carindoorarrival", "cia(car_arrived)")
			println("+++++++++ testweightsensor")
			var resultSensor = channelForUnitTesting.receive()
			assertTrue(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toInt() > 500 )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
		}
	}


	//@Test
	fun testOutsonar(){
 		myactor!!.solve("freedindoor","")
 		myactor!!.solve("freedoutdoor","")

		runBlocking{
			val channelForUnitTesting = Channel<String>()
			testingObserver!!.addObserver(channelForUnitTesting, "outdoorStatus")
			clientactor!!.emit("caroutdoorarrival", "coa(car_outdoor)" )
			println("+++++++++ testoutsonar Occupied")
			var resultSensor = channelForUnitTesting.receive()
			print(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString())
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "BUSY" )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
			clientactor!!.emit("carwithdrawn", "cw(bye)" )
			println("+++++++++ testoutsonar Freed")
			resultSensor = channelForUnitTesting.receive()
			assertEquals(resultSensor.substringAfter("(",resultSensor).substringBefore(")",resultSensor).toString(), "FREE" )
			println("+++++++++ testweightsensor RESULT=$resultSensor +++++++++")
		}
	}


}
