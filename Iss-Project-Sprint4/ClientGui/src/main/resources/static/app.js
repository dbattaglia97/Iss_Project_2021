var stompClient = null;

function setConnected(connected) {
console.log(" %%% app setConnected:" + connected );
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {

    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        //SUBSCRIBE to STOMP topic updated by the server
        stompClient.subscribe('/topic/infodisplay', function (msg) {
        //msg: {"content":"led(off):12"} or {"content":"sonarvalue(D)"}
             //alert(msg)
             var jsonMsg = JSON.parse(msg.body).content //JSON to String
             console.log("inConnect"+jsonMsg);
            //var mss = JSON.stringify(jsonMsg)
             if( jsonMsg.includes("ss")) {
                showMsg(  "SLOTNUM: "+jsonMsg.replace("ss",""), "infoDisplay" );

             }else if ( jsonMsg.includes("tt")) {
                showMsg( "TOKEN: "+jsonMsg.replace("tt",""), "infoDisplay" );

             }else if ( jsonMsg.includes("ww")) showMsg( "WAIT UNTIL INDOOR BE FREE!! SLOTNUM: "+jsonMsg.replace("ww",""), "infoDisplay" );
        });

    });
}

function disconnect() {
    if (stompClient !== null) {
    }
    setConnected(false);
    console.log("Disconnected");
}

function showMsg(message, outputId) {
console.log("inShow"+message);

    $("#"+outputId).html( "<pre>"+message.replace(/\n/g,"<br/>")+"</pre>" );
}



