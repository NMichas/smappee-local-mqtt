# Smappee MQTT
A (quick and dirty Java) client for a local Smappee hub.
 
This client connects to your local Smappee hub (no need to access the online API)
and:
 * pools for measurements and publish them to an MQTT topic.
 * listens to an MQTT topic for device control (on/off) messages.
  
  
## Configuration
The client needs a configuration file named `smappee-local-mqtt.conf` with the
following properties:
```
# Is measurements polling (and MQQT reporting) enabled?
polling=true
# Is devices control enabled?
control=true

# How often (in msec) to poll for new measurements.
poller.freq=5000

# The address of Smappee hub.
smappee.hub=mysmappee
# The password for Smappee web interface.
smappee.password=admin

# The address of the MQTT host.
mqtt.host=tcp://mymqtt:1883
# The username to connect with to MQTT (leave empty for anonymous).
mqtt.username=user
# The password to connect with to MQTT.
mqtt.password=pass

# The MQTT topic to publish measurements.
mqtt.polling.topic=topic1
# The MQTT topic to listen for control commands.
mqtt.control.topic=topic2
```

## How to get it
The binary of the client is published as a Maven artifact under:
```
<groupId>com.github.jitpack</groupId>
<artifactId>smappee-local-mqtt</artifactId>
<version>1.0.0</version>
```
Alternatively, you can also download it from GitHub:  
[smappee-local-mqtt](https://github.com/NMichas/smappee-local-mqtt/releases/download/1.0.0/smappee-local-mqtt-1.0.0.jar)

## How to run
`java -jar smappee-local-mqtt-1.0.0.jar`

## Messages
### Measurements
Your chosen MQTT topic receives measurements in the following JSON structure:
```
{  
   "voltage":"231.8",
   "phase1_current":"0.584",
   "phase1_activePower":"129.988",
   "phase1_reactivePower":"135.528",
   "phase1_apparentPower":"38.353",
   "phase1_cosfi":"95",
   "phase2_current":"3.726",
   "phase2_activePower":"712.394",
   "phase2_reactivePower":"863.909",
   "phase2_apparentPower":"488.705",
   "phase2_cosfi":"82",
   "phase3_current":"3.695",
   "phase3_activePower":"800.055",
   "phase3_reactivePower":"856.673",
   "phase3_apparentPower":"306.268",
   "phase3_cosfi":"93"
}
```
_Note: Currently only 3-phase, no solar power systems are supported, since this
is what I have to test with. Feel free to submit a PR for additional measurements.:_

### Control
You can control the devices (smart plugs) connected to your Smappee hub by publishing
messages to your chosen MQTT topic with the following JSON structure:

Turn on:
```
{
"id": 1,
"status": true
}
```

Turn off:
```
{
"id": 1,
"status": false
}
```