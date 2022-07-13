package it.unibo.clientGui

import com.andreapivetta.kolor.Color
import it.unibo.actor0.sysUtil
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.springframework.web.util.HtmlUtils
import java.io.*
import java.lang.Runnable
import java.lang.Exception
import java.net.*
import java.util.concurrent.TimeUnit

class Client : Runnable {
    lateinit var controller : HumanInterfaceController
    private var socket: Socket? = null
    private var input: BufferedReader? = null
    override fun run() {
        try {
            val serverAddr = InetAddress.getByName(SERVER_IP)
            socket = Socket(serverAddr, SERVERPORT)

            while (!Thread.currentThread().isInterrupted) {

                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                val message = input!!.readLine()

                val content = message.substringAfter("(",message).substringBefore(")",message)
                var lines = content.split(",").toTypedArray();
                val id          = lines[0]
                val type        = lines[1]
                val sender      = lines[2];
                val dest        = lines[3]
                val msg         = lines[4]+")"
                val msgArg      = msg?.substringAfter("(",msg)?.substringBefore(")",msg)
                println("%%%%% Received: $message")

                if(id == "enter"){
                    val jsonMsg = "{\"enter\":\"$msgArg\"}"
                    var jsonContent = JSONObject(jsonMsg)
                    val slotRep = ResourceRep("" + HtmlUtils.htmlEscape( "ss"+jsonContent.getString("enter")) )
                    println("slot: $msgArg")
                    TimeUnit.MILLISECONDS.sleep(500L)
                    controller.simpMessagingTemplate?.convertAndSend(WebSocketConfig.topicForClient, slotRep)
                }
                else if(id == "receipt"){
                    println("token: $msgArg")
                    val jsonMsg : String = "{\"tokenid\":\"$msgArg\"}"
                    var jsonContent : JSONObject = JSONObject(jsonMsg)
                    val tokenRep = ResourceRep("" + HtmlUtils.htmlEscape( "tt"+jsonContent.getString("tokenid")) )
                    controller.simpMessagingTemplate?.convertAndSend(WebSocketConfig.topicForClient, tokenRep)

                }
                else if(id == "waitIndoor"){
                    val jsonMsg = "{\"waitIndoor\":\"$msgArg\"}"
                    var jsonContent = JSONObject(jsonMsg)
                    val waitIndoorRep = ResourceRep("" + HtmlUtils.htmlEscape( "ww"+jsonContent.getString("waitIndoor")) )
                    controller.simpMessagingTemplate?.convertAndSend(WebSocketConfig.topicForClient, waitIndoorRep)
                }
                else if(id == "pickupelaborated"){
                    val jsonMsg = "{\"pickupelaborated\":\"$msgArg\"}"
                    var jsonContent = JSONObject(jsonMsg)
                    val pickupRep = ResourceRep("" + HtmlUtils.htmlEscape( "pe"+jsonContent.getString("pickupelaborated")) )
                    controller.simpMessagingTemplate?.convertAndSend(WebSocketConfig.topicForClient, pickupRep)
                }
            }
        } catch (e1: UnknownHostException) {
            e1.printStackTrace()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun forward(message: String?) {
        Thread {
            try {
                if (null != socket) {
                    val out = PrintWriter(
                        BufferedWriter(
                            OutputStreamWriter(socket!!.getOutputStream())), true
                    )
                    out.println(message)
                    println(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    companion object {
        const val SERVER_IP = "localhost" //ip del parkingmanagerservice
        const val SERVERPORT = 5683
    }
}


