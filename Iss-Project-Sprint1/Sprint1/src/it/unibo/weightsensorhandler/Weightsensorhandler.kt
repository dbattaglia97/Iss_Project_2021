/* Generated by AN DISI Unibo */ 
package it.unibo.weightsensorhandler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Weightsensorhandler ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		var Www=1 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("WeightsensorHandler INIT | WEIGHTSENSORHANDLER")
						KBSupport.init() 
					}
					 transition(edgeName="t014",targetState="working",cond=whenDispatch("startweightsensor"))
				}	 
				state("working") { //this:State
					action { //it:State
						println("WeightsensorHandler START | WEIGHTSENSORHANDLER")
						delay(1000) 
					}
					 transition(edgeName="t015",targetState="handleWeightData",cond=whenEvent("weight"))
				}	 
				state("handleWeightData") { //this:State
					action { //it:State
						println("WeightsensorHandler handling weight | WEIGHTSENSORHANDLER")
						if( checkMsgContent( Term.createTerm("weight(W)"), Term.createTerm("weight(W)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Www = payloadArg(0).toInt()
								            println("Weight: " + Www) 
								if(Www>=500){ 
								KBSupport.changeIndoorToOccupied() 
								forward("weightcheck", "wc(OK)" ,"parkingmanagerservice" ) 
								}else{ 
								forward("weightcheck", "wc(NOTOK)" ,"parkingmanagerservice" ) 
								} 
								forward("updateForTesting", "weight($Www)" ,"testupdater" ) 
						}
					}
					 transition( edgeName="goto",targetState="working", cond=doswitch() )
				}	 
			}
		}
}
