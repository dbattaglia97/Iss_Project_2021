/* Generated by AN DISI Unibo */ 
package it.unibo.weightsensor

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Weightsensor ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		var P = 0 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Weightsensor starts | WEIGHTSENSOR")
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("ready") { //this:State
					action { //it:State
						println("Weightsensor waiting | WEIGHTSENSOR")
					}
					 transition(edgeName="t013",targetState="simulate",cond=whenEvent("carindoorarrival"))
				}	 
				state("simulate") { //this:State
					action { //it:State
						println("Weight simulation | WEIGHTSENSOR")
						 P =kotlin.random.Random.nextInt(500,4000)  
						emit("weight", "weight($P)" ) 
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
			}
		}
}
