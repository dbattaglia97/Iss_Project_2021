/* Generated by AN DISI Unibo */ 
package it.unibo.parkingmanagerservice

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Parkingmanagerservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			lateinit var outsonarActor	: ActorBasic
			lateinit var testUpdaterActor	: ActorBasic
			lateinit var guiUpdaterActor	: ActorBasic
			var alreadythere=false
			var prog= 0
			var SLOTNUM=-1
			var CARSLOTNUM=-1
			var TOKENIN= -1
			var notAllowed=false
			var boolIN=false
			var boolOUT=false
			var weightCheck=""
			var location=""
			var outsonarSimulated=true
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						solve("consult('sysRules.pl')","") //set resVar	
						solve("consult('parkingAreaKb.pl')","") //set resVar	
						solve("outsonarSimulated","") //set resVar	
						if( currentSolution.isSuccess() ) {println("Outsonar simulato | SERVICE")
						outsonarActor= sysUtil.getActor("outsonar")!!
										outsonarSimulated=true 
						}
						else
						{outsonarSimulated=false 
						println("Outsonar reale | SERVICE")
						}
						guiUpdaterActor= sysUtil.getActor("guiupdater")!!
								testUpdaterActor= sysUtil.getActor("testupdater")!! 
					}
					 transition(edgeName="t010",targetState="setup",cond=whenDispatch("startManager"))
				}	 
				state("setup") { //this:State
					action { //it:State
						delay(2000) 
						forward("systemready", "systemready(X)" ,"trolley" ) 
						delay(50) 
						forward("updateGui", "indoorStatus(FREE)" ,"guiupdater" ) 
						delay(50) 
						forward("updateGui", "outdoorStatus(FREE)" ,"guiupdater" ) 
						delay(50) 
						forward("updateGui", "fan(OFF)" ,"guiupdater" ) 
						delay(50) 
						forward("updateGui", "weight(0)" ,"guiupdater" ) 
						delay(50) 
						forward("updateGui", "temp(20)" ,"guiupdater" ) 
						delay(50) 
						forward("updateGui", "alarm(OFF)" ,"guiupdater" ) 
						forward("updateForTesting", "indoorAtStart(FREE)" ,"testupdater" ) 
						delay(100) 
						forward("updateForTesting", "outdoorAtStart(FREE)" ,"testupdater" ) 
						delay(100) 
						forward("updateForTesting", "posRobot(0,0)" ,"testupdater" ) 
						forward("startsonar", "sonar(ON)" ,"sonarhandler" ) 
						forward("startweightsensor", "sensor(ON)" ,"weightsensorhandler" ) 
						forward("startthermometer", "startthermometer(X)" ,"thermometer" ) 
						println("Park System START | SERVICE")
						stateTimer = TimerActor("timer_setup", 
							scope, context!!, "local_tout_parkingmanagerservice_setup", 1000.toLong() )
					}
					 transition(edgeName="t011",targetState="checkAcceptIN",cond=whenTimeout("local_tout_parkingmanagerservice_setup"))   
				}	 
				state("checkAcceptIN") { //this:State
					action { //it:State
						println("Checking if an AcceptIN can be elaborated| SERVICE")
						solve("availableParking","") //set resVar	
						if( currentSolution.isSuccess() ) {boolIN=true 
						}
						else
						{boolIN=false 
						}
					}
					 transition( edgeName="goto",targetState="checkOutdoor", cond=doswitchGuarded({boolIN==true 
					}) )
					transition( edgeName="goto",targetState="checkOnlyOutdoor", cond=doswitchGuarded({! (boolIN==true 
					) }) )
				}	 
				state("checkOutdoor") { //this:State
					action { //it:State
						println("Checking if an AcceptOut can be elaborated| SERVICE")
						solve("acceptOUT","") //set resVar	
						if( currentSolution.isSuccess() ) {boolOUT=true 
						}
						else
						{boolOUT=false 
						}
					}
					 transition( edgeName="goto",targetState="allReady", cond=doswitchGuarded({boolOUT==true 
					}) )
					transition( edgeName="goto",targetState="indoorOnlyReady", cond=doswitchGuarded({! (boolOUT==true 
					) }) )
				}	 
				state("checkOnlyOutdoor") { //this:State
					action { //it:State
						println("Checking if an AcceptOut can be elaborated| SERVICE")
						solve("accetOUT","") //set resVar	
						if( currentSolution.isSuccess() ) {boolOUT=true 
						}
						else
						{boolOUT=false 
						}
					}
					 transition( edgeName="goto",targetState="outdoorOnlyReady", cond=doswitchGuarded({boolOUT==true 
					}) )
					transition( edgeName="goto",targetState="waiting", cond=doswitchGuarded({! (boolOUT==true 
					) }) )
				}	 
				state("indoorOnlyReady") { //this:State
					action { //it:State
						println("The system can only elaborate AcceptIN request| SERVICE")
						boolOUT=false
						        boolIN=false  
						stateTimer = TimerActor("timer_indoorOnlyReady", 
							scope, context!!, "local_tout_parkingmanagerservice_indoorOnlyReady", 10000.toLong() )
					}
					 transition(edgeName="t012",targetState="moveToHome",cond=whenTimeout("local_tout_parkingmanagerservice_indoorOnlyReady"))   
					transition(edgeName="t013",targetState="acceptin",cond=whenRequest("reqenter"))
				}	 
				state("outdoorOnlyReady") { //this:State
					action { //it:State
						println("The system can only elaborate AcceptOUT request| SERVICE")
						boolOUT=false
						        boolIN=false  
						stateTimer = TimerActor("timer_outdoorOnlyReady", 
							scope, context!!, "local_tout_parkingmanagerservice_outdoorOnlyReady", 10000.toLong() )
					}
					 transition(edgeName="t014",targetState="moveToHome",cond=whenTimeout("local_tout_parkingmanagerservice_outdoorOnlyReady"))   
					transition(edgeName="t015",targetState="acceptout",cond=whenRequest("pickup"))
				}	 
				state("waiting") { //this:State
					action { //it:State
						println("The system can't elaborate any request| SERVICE")
						stateTimer = TimerActor("timer_waiting", 
							scope, context!!, "local_tout_parkingmanagerservice_waiting", 5000.toLong() )
					}
					 transition(edgeName="t016",targetState="moveToHome",cond=whenTimeout("local_tout_parkingmanagerservice_waiting"))   
				}	 
				state("moveToHome") { //this:State
					action { //it:State
						notAllowed=false 
						if(!TrolleyPlannerSupport.alreadyThere(0)){ 
						println("Moving Trolley to HOME")
						forward("trolleycmd", "trolleycmd(moveToHome)" ,"trolley" ) 
						forward("updateForTesting", "movingTo(HOME)" ,"testupdater" ) 
						} 
						stateTimer = TimerActor("timer_moveToHome", 
							scope, context!!, "local_tout_parkingmanagerservice_moveToHome", 3000.toLong() )
					}
					 transition(edgeName="t017",targetState="checkAcceptIN",cond=whenTimeout("local_tout_parkingmanagerservice_moveToHome"))   
				}	 
				state("allReady") { //this:State
					action { //it:State
						println("The system can elaborate AcceptIN and AcceptOut requests| SERVICE")
						stateTimer = TimerActor("timer_allReady", 
							scope, context!!, "local_tout_parkingmanagerservice_allReady", 10000.toLong() )
					}
					 transition(edgeName="t018",targetState="moveToHome",cond=whenTimeout("local_tout_parkingmanagerservice_allReady"))   
					transition(edgeName="t019",targetState="acceptin",cond=whenRequest("reqenter"))
					transition(edgeName="t020",targetState="acceptout",cond=whenRequest("pickup"))
				}	 
				state("acceptin") { //this:State
					action { //it:State
						println("The system is elaborating an AcceptIN request| SERVICE")
						solve("availableParking","") //set resVar	
						if( currentSolution.isSuccess() ) {solve("slotFree(S)","") //set resVar	
						 SLOTNUM = getCurSol("S").toString().toInt() 
						}
						else
						{SLOTNUM=0 
						}
						solve("acceptIN","") //set resVar	
						if( currentSolution.isSuccess() ) {forward("updateForTesting", "slotnum($SLOTNUM)" ,"testupdater" ) 
						println("Reply to reqenter with $SLOTNUM  | SERVICE")
						answer("reqenter", "enter", "enter($SLOTNUM)"   )  
						}
						else
						{forward("updateForTesting", "waitIndoor($SLOTNUM)" ,"testupdater" ) 
						notAllowed=true 
						println("Reply to reqenter with waitIndoor($SLOTNUM)  | SERVICE")
						answer("reqenter", "waitIndoor", "waitIndoor($SLOTNUM)"   )  
						}
					}
					 transition( edgeName="goto",targetState="carenterCanBeAccepted", cond=doswitchGuarded({notAllowed==false 
					}) )
					transition( edgeName="goto",targetState="moveToHome", cond=doswitchGuarded({! (notAllowed==false 
					) }) )
				}	 
				state("carenterCanBeAccepted") { //this:State
					action { //it:State
					}
					 transition(edgeName="t021",targetState="carenter",cond=whenRequest("carenter"))
				}	 
				state("carenter") { //this:State
					action { //it:State
						println("The system is elaborating a carenter request| SERVICE")
						 prog++ 
						println("carindoorarrival emitted in order to be processed by  WEIGHT SENSOR   | SERVICE")
						emit("carindoorarrival", "cia(car_arrived)" ) 
					}
					 transition(edgeName="t022",targetState="weightCheck",cond=whenDispatch("weightcheck"))
				}	 
				state("weightCheck") { //this:State
					action { //it:State
						println("The system is checking if there is a car in the indoorArea| SERVICE")
						if( checkMsgContent( Term.createTerm("wc(V)"), Term.createTerm("wc(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								weightCheck = payloadArg(0).toString() 
								println("Weight check: $weightCheck")
						}
					}
					 transition( edgeName="goto",targetState="moveToIn", cond=doswitchGuarded({weightCheck.equals("OK") 
					}) )
					transition( edgeName="goto",targetState="weightNotOK", cond=doswitchGuarded({! (weightCheck.equals("OK") 
					) }) )
				}	 
				state("weightNotOK") { //this:State
					action { //it:State
						println("There isn't a car in the indoor")
					}
					 transition( edgeName="goto",targetState="moveToHome", cond=doswitch() )
				}	 
				state("moveToIn") { //this:State
					action { //it:State
						println("Trolley is moving to Indoor")
						forward("trolleycmd", "trolleycmd(moveToIn)" ,"trolley" ) 
					}
					 transition(edgeName="t023",targetState="trolleyIsInIndoor",cond=whenEvent("finishedPath"))
				}	 
				state("trolleyIsInIndoor") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("finishedPath(V)"), Term.createTerm("finishedPath(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								location = payloadArg(0).toString()
											location= location.substringAfter("(",location).substringBefore(")",location)
											println("Location: " + location + " || trolleyIsInIndoor")
											forward("updateForTesting", "posRobot($location)" ,"testupdater" )
						}
					}
					 transition( edgeName="goto",targetState="receipt", cond=doswitch() )
				}	 
				state("receipt") { //this:State
					action { //it:State
						delay(1000) 
						solve("freedindoor","") //set resVar	
						forward("updateGui", "indoorStatus(FREE)" ,"guiupdater" ) 
						delay(50) 
						forward("updateForTesting", "indoorStatus(FREE)" ,"testupdater" ) 
						forward("updateGui", "weight(0)" ,"guiupdater" ) 
						 var TOKENID = "$prog$SLOTNUM" 
						solve("addToken($TOKENID)","") //set resVar	
						println("REPLY TO CARENTER WITH RECEIPT $TOKENID | SERVICE")
						answer("carenter", "receipt", "receipt($TOKENID)"   )  
						forward("updateForTesting", "receipt($TOKENID)" ,"testupdater" ) 
						stateTimer = TimerActor("timer_receipt", 
							scope, context!!, "local_tout_parkingmanagerservice_receipt", 1000.toLong() )
					}
					 transition(edgeName="t024",targetState="moveToSlotIn",cond=whenTimeout("local_tout_parkingmanagerservice_receipt"))   
				}	 
				state("moveToSlotIn") { //this:State
					action { //it:State
						delay(1000) 
						println("SLOTNUM IS $SLOTNUM")
						solve("occupySlot($SLOTNUM)","") //set resVar	
						println("Trolley moves from entrance to slot $SLOTNUM | SERVICE")
							var MOVETOSLOT = "moveToSlot".plus(SLOTNUM) 
						forward("trolleycmd", "trolleycmd($MOVETOSLOT)" ,"trolley" ) 
					}
					 transition(edgeName="t025",targetState="trolleyIsInSlot",cond=whenEvent("finishedPath"))
				}	 
				state("trolleyIsInSlot") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("finishedPath(V)"), Term.createTerm("finishedPath(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								location = payloadArg(0).toString()
								            location= location.substringAfter("(",location).substringBefore(")",location)
								            println("Location: " + location + " || trolleyIsInSlot")
								            forward("updateForTesting", "posRobot($location)" ,"testupdater" )
						}
					}
					 transition( edgeName="goto",targetState="checkAcceptIN", cond=doswitch() )
				}	 
				state("acceptout") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("pickup(TOKENID)"), Term.createTerm("pickup(TOKENIN)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 TOKENIN = payloadArg(0).toInt()  
								println("Token provided by the customer for the pickup $TOKENIN | SERVICE")
								solve("token($TOKENIN)","") //set resVar	
								if( currentSolution.isSuccess() ) {answer("pickup", "pickupelaborated", "pickupelaborated($TOKENIN)"   )  
								CARSLOTNUM= TOKENIN%10 
								println("Token elaboration OK, token= $TOKENIN and corresponding slot= $CARSLOTNUM| SERVICE")
								forward("updateForTesting", "carslotnum($CARSLOTNUM)" ,"testupdater" ) 
								}
								else
								{TOKENIN=-1 
								answer("pickup", "pickupelaborated", "pickupelaborated($TOKENIN)"   )  
								println("TOKEN NOT OK | SERVICE")
								forward("updateForTesting", "pickupNotAccepted($TOKENIN)" ,"testupdater" ) 
								}
						}
					}
					 transition( edgeName="goto",targetState="checkAlreadyInSlot", cond=doswitchGuarded({TOKENIN>0 
					}) )
					transition( edgeName="goto",targetState="tokenError", cond=doswitchGuarded({! (TOKENIN>0 
					) }) )
				}	 
				state("tokenError") { //this:State
					action { //it:State
						println("TOKEN ERROR | SERVICE")
					}
					 transition( edgeName="goto",targetState="checkAcceptIN", cond=doswitch() )
				}	 
				state("checkAlreadyInSlot") { //this:State
					action { //it:State
						alreadythere=TrolleyPlannerSupport.alreadyThere(SLOTNUM) 
					}
					 transition( edgeName="goto",targetState="picking", cond=doswitchGuarded({alreadythere==true 
					}) )
					transition( edgeName="goto",targetState="goToSlot", cond=doswitchGuarded({! (alreadythere==true 
					) }) )
				}	 
				state("goToSlot") { //this:State
					action { //it:State
						println("Trolley moves to slot $SLOTNUM | SERVICE")
							var MOVETOSLOT = "moveToSlot".plus(SLOTNUM) 
						forward("trolleycmd", "trolleycmd($MOVETOSLOT)" ,"trolley" ) 
					}
					 transition(edgeName="t026",targetState="picking",cond=whenEvent("finishedPath"))
				}	 
				state("picking") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("finishedPath(V)"), Term.createTerm("finishedPath(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								location = payloadArg(0).toString()
											location= location.substringAfter("(",location).substringBefore(")",location)
											println("Location: " + location )
											forward("updateForTesting", "posRobot($location)" ,"testupdater" )
						}
						println("Trolley is picking a car | SERVICE")
						println("Trolley picking car from slot $CARSLOTNUM result from $TOKENIN % 10 | SERVICE")
						solve("liberaSlot($CARSLOTNUM)","") //set resVar	
						solve("removeToken($TOKENIN)","") //set resVar	
					}
					 transition( edgeName="goto",targetState="moveToOut", cond=doswitch() )
				}	 
				state("moveToOut") { //this:State
					action { //it:State
						forward("trolleycmd", "trolleycmd(moveToOut)" ,"trolley" ) 
					}
					 transition(edgeName="t027",targetState="trolleyIsInOutdoor",cond=whenEvent("finishedPath"))
				}	 
				state("trolleyIsInOutdoor") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("finishedPath(V)"), Term.createTerm("finishedPath(V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								location = payloadArg(0).toString()
											location= location.substringAfter("(",location).substringBefore(")",location)
											println("Location: " + location + " || trolleyIsInOutdoor")
											forward("updateForTesting", "posRobot($location)" ,"testupdater" )
						}
						println("Car is in Outdoor area | SERVICE")
						if(outsonarSimulated){ 
						emit("caroutdoorarrival", "coa(car_outdoor)" ) 
						delay(2000) 
						emit("carwithdrawn", "cw(bye)" ) 
						} 
						stateTimer = TimerActor("timer_trolleyIsInOutdoor", 
							scope, context!!, "local_tout_parkingmanagerservice_trolleyIsInOutdoor", 2000.toLong() )
					}
					 transition(edgeName="t028",targetState="checkAcceptIN",cond=whenTimeout("local_tout_parkingmanagerservice_trolleyIsInOutdoor"))   
				}	 
			}
		}
}
