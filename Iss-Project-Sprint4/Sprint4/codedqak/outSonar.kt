import it.unibo.kactor.MsgUtil
import kotlinx.coroutines.delay
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import alice.tuprolog.Term
import alice.tuprolog.Struct
import java.util.Timer
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import it.unibo.kactor.ActorBasicFsm


class outSonar (name : String ) : ActorBasic( name ) {

	private var mainScope = CoroutineScope(Dispatchers.Default)
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
	    override suspend fun actorBody(msg: ApplMessage) {
	  		if( msg.msgId() == "caroutdoorarrival" &&  msg.msgType() == "event") distanceSimulationOccupied()
	  		else if( msg.msgId() == "carwithdrawn" &&  msg.msgType() == "event") distanceSimulationFreed()
	 	}
		
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
		  suspend fun distanceSimulationOccupied(){
			 //updategui
				println("Emitting distance 15 -> car is in outdoor")
				emit("sonaroutdoor", "distance(15)" ) 
			}
		
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi
		  suspend fun distanceSimulationFreed(){
				println("Emitting distance 50 -> car is NOT ANYMORE in outdoor")
				emit("sonaroutdoor", "distance(50)" ) 
			}

}
