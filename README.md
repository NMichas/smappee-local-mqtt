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
is what I have to test with. Feel free to submit a PR for additional measurements._

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

## Bonus items
### Integration in Home Assistant
#### Getting measurements
```
sensor voltage:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Voltage"
    unit_of_measurement: "V"
    value_template: "{{value_json.voltage}}"

sensor phase1_current:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Current - phase 1"
    unit_of_measurement: "A"
    value_template: "{{value_json.phase1_current}}"

sensor phase2_current:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Current - phase 2"
    unit_of_measurement: "A"
    value_template: "{{value_json.phase2_current}}"

sensor phase3_current:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Current - phase 3"
    unit_of_measurement: "A"
    value_template: "{{value_json.phase3_current}}"

sensor phase1_activePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Active power - phase 1"
    unit_of_measurement: "W"
    value_template: "{{value_json.phase1_activePower}}"

sensor phase2_activePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Active power - phase 2"
    unit_of_measurement: "W"
    value_template: "{{value_json.phase2_activePower}}"

sensor phase3_activePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Active power - phase 3"
    unit_of_measurement: "W"

sensor phase1_reactivePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Reactive power - phase 1"
    unit_of_measurement: "var"
    value_template: "{{value_json.phase1_reactivePower}}"

sensor phase2_reactivePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Reactive power - phase 2"
    unit_of_measurement: "var"
    value_template: "{{value_json.phase2_reactivePower}}"

sensor phase3_reactivePower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Reactive power - phase 3"
    unit_of_measurement: "var"
    value_template: "{{value_json.phase3_reactivePower}}"

sensor phase1_apparentPower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Apparent power - phase 1"
    unit_of_measurement: "VA"
    value_template: "{{value_json.phase1_apparentPower}}"

sensor phase2_apparentPower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Apparent power - phase 2"
    unit_of_measurement: "VA"
    value_template: "{{value_json.phase2_apparentPower}}"

sensor phase3_apparentPower:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Apparent power - phase 3"
    unit_of_measurement: "VA"
    value_template: "{{value_json.phase3_apparentPower}}"

sensor phase1_cosfi:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Cos(φ) - phase 1"
    value_template: "{{value_json.phase1_cosfi}}"

sensor phase2_cosfi:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Cos(φ) - phase 2"
    value_template: "{{value_json.phase2_cosfi}}"

sensor phase3_cosfi:
  - platform: mqtt
    state_topic: "hass/smappee/measurements"
    name: "Cos(φ) - phase 3"
    value_template: "{{value_json.phase3_cosfi}}"
    
sensor templates:
  - platform: template
    sensors:
      total_power:
        friendly_name: "Total power"
        unit_of_measurement: "W"
        value_template: "{{ states('sensor.active_power__phase_1')|int  + states('sensor.active_power__phase_2')|int + states('sensor.active_power__phase_3')|int }}"
      total_current:
        friendly_name: "Total current"
        unit_of_measurement: "A"
        value_template: "{{ states('sensor.current__phase_1')|int  + states('sensor.current__phase_2')|int + states('sensor.current__phase_3')|int }}"
    
```

#### Controlling devices
```
switch smappe_switch1:
  platform: mqtt
  command_topic: 'hass/smappee/control'
  payload_on: '{"id": 1, "status": true}'
  payload_off: '{"id": 1, "status": false}'
```

### Systemd configuration
Create `/lib/systemd/system/smappee-client.service` with the following content:

```
[Unit]
Description=Smappee client Daemon

[Service]
WorkingDirectory=/home/myuser/smappee
ExecStart=/usr/bin/java -jar smappee-local-mqtt-1.0.0.jar
User=myuser

[Install]
WantedBy=multi-user.target
```

Substituting `myuser` with the user you want to run the client with. Don't forget to
place `smappee-local-mqtt.conf` and `smappee-local-mqtt-1.0.0.jar` under the location you specify in WorkingDirectory.

* Start the service  
  `sudo systemctl start smappee-client`
* Stop the service  
  `sudo systemctl stop smappee-client`
* Automatically start the service on boot  
  `sudo systemctl enable smappee-client`