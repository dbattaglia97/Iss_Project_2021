%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "8092").
 qactor( testupdater, ctxcarparking, "testUpdater").
  qactor( client, ctxcarparking, "it.unibo.client.Client").
  qactor( outsonar, ctxcarparking, "it.unibo.outsonar.Outsonar").
  qactor( sonarhandler, ctxcarparking, "it.unibo.sonarhandler.Sonarhandler").
  qactor( timer, ctxcarparking, "it.unibo.timer.Timer").
  qactor( weightsensor, ctxcarparking, "it.unibo.weightsensor.Weightsensor").
  qactor( weightsensorhandler, ctxcarparking, "it.unibo.weightsensorhandler.Weightsensorhandler").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
  qactor( basicrobot, ctxcarparking, "it.unibo.basicrobot.Basicrobot").
