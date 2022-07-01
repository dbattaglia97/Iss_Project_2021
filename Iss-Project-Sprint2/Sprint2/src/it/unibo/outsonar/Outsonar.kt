/* Generated by AN DISI Unibo */ 
package it.unibo.outsonar

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Outsonar ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
				state("working") { //this:State
					action { //it:State
						println("Sonar working")
					}
					 transition(edgeName="t06",targetState="distanceSimulationOccupied",cond=whenEvent("caroutdoorarrival"))
					transition(edgeName="t07",targetState="distanceSimulationFreed",cond=whenEvent("carwithdrawn"))
				}	 
				state("distanceSimulationOccupied") { //this:State
					action { //it:State
						println("Emitting distance 15 -> car is in outdoor")
						emit("sonaroutdoor", "distance(15)" ) 
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
				state("distanceSimulationFreed") { //this:State
					action { //it:State
						println("Emitting distance 50 -> car is NOT ANYMORE in outdoor")
						emit("sonaroutdoor", "distance(50)" ) 
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
			}
		}
}