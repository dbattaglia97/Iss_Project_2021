import java.util.ArrayList

object TrolleyPlannerSupport{
	var slotColumn=0
	var listCommand = arrayListOf<String>()
	var moveToOut = false
	var moveToIn  = false
	fun setGoal( goal : String ){

		when(goal){

			"moveToIn" ->  {	itunibo.planner.plannerUtil.planForGoal("6","0")
								moveToIn = true
							}
			"moveToSlot1" -> {
								itunibo.planner.plannerUtil.planForGoal("1","1")
								slotColumn = 1
								}
			"moveToSlot2" -> {
								itunibo.planner.plannerUtil.planForGoal("4","1")
								slotColumn = 4
								}
			"moveToSlot3" -> {
								itunibo.planner.plannerUtil.planForGoal("1","2")
								slotColumn = 1
								}
			"moveToSlot4" -> {
								itunibo.planner.plannerUtil.planForGoal("4","2")
								slotColumn = 4
							}
			"moveToSlot5" -> {
								itunibo.planner.plannerUtil.planForGoal("1","3")
								slotColumn = 1
								}
			"moveToSlot6" -> {
								itunibo.planner.plannerUtil.planForGoal("4","3")
								slotColumn = 4
								}

			"moveToOut"  -> {   itunibo.planner.plannerUtil.planForGoal("6","4")
								moveToOut = true
							}
			"moveToHome" -> itunibo.planner.plannerUtil.planForGoal("0","0")
			"end" -> itunibo.planner.plannerUtil.planForGoal("0","0")
		}
	}
	fun initPlanner(mapname: String){
			itunibo.planner.plannerUtil.initAI(  )
			println("&&&  trolley loads the parking map from the given file ...")
			itunibo.planner.plannerUtil.loadRoomMap( "$mapname"  )
			itunibo.planner.plannerUtil.showMap(  )
			itunibo.planner.plannerUtil.showCurrentRobotState(  )
	}

	fun getNextMove(): String{
		var move = itunibo.planner.plannerUtil.getNextPlannedMove()
		if(move.length>0){
			itunibo.planner.plannerUtil.updateMap(  "$move" )
			itunibo.planner.plannerUtil.showCurrentRobotState( )
		}

		return move
	}
	fun atHome(): ArrayList<String>{
		listCommand.clear()
		var direction= itunibo.planner.plannerUtil.getDirection()
		if(direction == "leftDir"){
			itunibo.planner.plannerUtil.updateMap( "l"  )
			listCommand.add("l")
		}else{
			itunibo.planner.plannerUtil.updateMap( "l"  )
			itunibo.planner.plannerUtil.updateMap( "l"  )
			listCommand.add("l")
			listCommand.add("l")
		}
		itunibo.planner.plannerUtil.showCurrentRobotState( )
		return listCommand
	}
	fun getPosition() : String{
		return itunibo.planner.plannerUtil.get_curPos().toString()
	}
	
	fun loadUnloadCar(): ArrayList<String> {
		listCommand.clear()
		var direction= itunibo.planner.plannerUtil.getDirection()
		if(moveToIn == false && moveToOut ==false ){
			if(direction == "leftDir" ||  direction == "rightDir" ){
				itunibo.planner.plannerUtil.updateMap( "l"  )
				itunibo.planner.plannerUtil.updateMap( "l"  )
				listCommand.add("l")
				listCommand.add("l")
			}else if(direction == "upDir"){
				if(slotColumn == 4){
					itunibo.planner.plannerUtil.updateMap( "r"  )
					listCommand.add("r")
				}else{
					itunibo.planner.plannerUtil.updateMap( "l"  )
					listCommand.add("l")
			}
			}else if(direction == "downDir"){
				if(slotColumn == 4){
					itunibo.planner.plannerUtil.updateMap( "l"  )
					listCommand.add("l")
				}else{
					itunibo.planner.plannerUtil.updateMap( "r"  )
					listCommand.add("r")
				}
				}
		}else{
			if(direction == "rightDir" && moveToIn == true){
				itunibo.planner.plannerUtil.updateMap( "r"  )
				listCommand.add("r")

			}else if(direction == "rightDir" && moveToOut == true){
				itunibo.planner.plannerUtil.updateMap( "l"  )
				listCommand.add("l")

			}else if(direction == "upDir" || direction == "downDir" ){
				itunibo.planner.plannerUtil.updateMap( "r"  )
				itunibo.planner.plannerUtil.updateMap( "r"  )
				listCommand.add("r")
				listCommand.add("r")
			}
			moveToOut =false
			moveToIn =false
		}
		itunibo.planner.plannerUtil.showCurrentRobotState( )
		for (command in listCommand) {
   			println("Command: "+command)
		}
		return listCommand
	}
}
