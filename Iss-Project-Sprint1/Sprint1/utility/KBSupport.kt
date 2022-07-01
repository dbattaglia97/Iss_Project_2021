//KBSupport
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
import alice.tuprolog.*

object KBSupport {
    var myactor : ActorBasic? = null

  fun init(){
    myactor = QakContext.getActor("parkingmanagerservice")
  }

fun changeIndoorToOccupied(){
    myactor!!.solve("occupiedindoor","")
  }

  fun changeIndoorToFree(){
    myactor!!.solve("freedindoor","")
  }

  fun changeOutdoorToOccupied(){
    myactor!!.solve("occupiedoutdoor","")
  }
  fun changeOutdoorToFree(){
    myactor!!.solve("freedoutdoor","")
  }
}
