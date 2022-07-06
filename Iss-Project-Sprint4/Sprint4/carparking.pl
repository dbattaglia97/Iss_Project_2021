%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "5683").
 qactor( testupdater, ctxcarparking, "testUpdater").
  qactor( guiupdater, ctxcarparking, "guiUpdater").
  qactor( outsonar, ctxcarparking, "outSonar").
  qactor( client, ctxcarparking, "it.unibo.client.Client").
  qactor( sonarhandler, ctxcarparking, "it.unibo.sonarhandler.Sonarhandler").
  qactor( timer, ctxcarparking, "it.unibo.timer.Timer").
  qactor( weightsensor, ctxcarparking, "it.unibo.weightsensor.Weightsensor").
  qactor( weightsensorhandler, ctxcarparking, "it.unibo.weightsensorhandler.Weightsensorhandler").
  qactor( parkingmanagerservice, ctxcarparking, "it.unibo.parkingmanagerservice.Parkingmanagerservice").
  qactor( trolley, ctxcarparking, "it.unibo.trolley.Trolley").
  qactor( basicrobot, ctxcarparking, "it.unibo.basicrobot.Basicrobot").
  qactor( fan, ctxcarparking, "it.unibo.fan.Fan").
  qactor( thermometer, ctxcarparking, "it.unibo.thermometer.Thermometer").
  qactor( fanautomatizer, ctxcarparking, "it.unibo.fanautomatizer.Fanautomatizer").
  qactor( trolleystopper, ctxcarparking, "it.unibo.trolleystopper.Trolleystopper").
