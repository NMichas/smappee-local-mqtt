# Smappee MQTT
A client for a local Smappee hub.
 
This client connects to your local Smappee hub (no need to access the online API)
and:
 * Polls for measurements and publish them to an MQTT topic.
 * Listens to an MQTT topic for device control (on/off) messages.

## How to run it
```docker
docker run \
	-d \
	--name slm \
	--restart always \
	-e "slm_polling=true" \
	-e "slm_control=false" \
	-e "slm_poller_freq=5000" \
	-e "slm_smappee_hub=smappe-url" \
	-e "slm_smappee_password=admin" \
	-e "slm_mqtt_host=tcp://mqtt:1883" \
	-e "slm_mqtt_polling_topic=slm/measurements" \
	-e "slm_mqtt_control_topic=slm/control" \
nassos/slm:2.0-amd64
```

You can also find Docker images for arm64 (slm:2.0-arm64), and armv7 (slm:2.0-amdv7).

## Messages
### Measurements
Your chosen MQTT topic receives measurements in the following JSON structure:
```json
{
  "voltage" : 232.5,
  "phase1_current" : 0.725,
  "phase1_activePower" : 123,
  "phase1_reactivePower" : 168,
  "phase1_apparentPower" : 114,
  "phase1_cosfi" : 72,
  "phase2_current" : 1.402,
  "phase2_activePower" : 236,
  "phase2_reactivePower" : 326,
  "phase2_apparentPower" : 224,
  "phase2_cosfi" : 72,
  "phase3_current" : 2.774,
  "phase3_activePower" : 508,
  "phase3_reactivePower" : 645,
  "phase3_apparentPower" : 397,
  "phase3_cosfi" : 78,
  "total_current" : 4.901,
  "total_activePower" : 867,
  "total_reactivePower" : 1139,
  "total_apparentPower" : 735
}
```
_Note: Currently only 3-phase, no solar power systems are supported, since this
is what I have to test with. Feel free to submit a PR for additional measurements._

### Control
You can control the devices (smart plugs) connected to your Smappee hub by publishing
messages to your chosen MQTT topic with the following JSON structure:

Turn on:
```json
{
"id": 1,
"status": true
}
```

Turn off:
```json
{
"id": 1,
"status": false
}
```

## Bonus items
### Integration in Home Assistant
#### Getting measurements
```yaml
- platform: mqtt
  state_topic: "slm/measurements"
  name: "Voltage"
  unique_id: "smappee_voltage"
  unit_of_measurement: "V"
  value_template: "{{value_json.voltage}}"
  device_class: voltage
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Current - phase 1"
  unique_id: "smappee_p1_current"
  unit_of_measurement: "A"
  value_template: "{{value_json.phase1_current}}"
  device_class: current
  icon: mdi:sine-wave

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Current - phase 2"
  unique_id: "smappee_p2_current"
  unit_of_measurement: "A"
  value_template: "{{value_json.phase2_current}}"
  device_class: current
  icon: mdi:sine-wave

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Current - phase 3"
  unique_id: "smappee_p3_current"
  unit_of_measurement: "A"
  value_template: "{{value_json.phase3_current}}"
  device_class: current
  icon: mdi:sine-wave

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Active power - phase 1"
  unique_id: "smappee_p1_active_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase1_activePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Active power - phase 2"
  unique_id: "smappee_p2_active_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase2_activePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Active power - phase 3"
  unique_id: "smappee_p3_active_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase3_activePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Reactive power - phase 1"
  unique_id: "smappee_p1_reactive_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase1_reactivePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Reactive power - phase 2"
  unique_id: "smappee_p2_reactive_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase2_reactivePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Reactive power - phase 3"
  unique_id: "smappee_p3_reactive_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase3_reactivePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Apparent power - phase 1"
  unique_id: "smappee_p1_apparent_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase1_apparentPower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Apparent power - phase 2"
  unique_id: "smappee_p2_apparent_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase2_apparentPower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Apparent power - phase 3"
  unique_id: "smappee_p3_apparent_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.phase3_apparentPower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Cos(φ) - phase 1"
  unique_id: "smappee_p1_cosf"
  value_template: "{{value_json.phase1_cosfi}}"
  device_class: power_factor
  icon: mdi:math-cos

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Cos(φ) - phase 2"
  unique_id: "smappee_p2_cosf"
  value_template: "{{value_json.phase2_cosfi}}"
  device_class: power_factor
  icon: mdi:math-cos

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Cos(φ) - phase 3"
  unique_id: "smappee_p3_cosf"
  value_template: "{{value_json.phase3_cosfi}}"
  device_class: power_factor
  icon: mdi:math-cos

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Current (total)"
  unique_id: "smappee_total_current"
  unit_of_measurement: "A"
  value_template: "{{value_json.total_current}}"
  device_class: current
  icon: mdi:sine-wave

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Reactive power (total)"
  unique_id: "smappee_total_reactive_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.total_reactivePower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Apparent power (total)"
  unique_id: "smappee_total_apparent_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.total_apparentPower}}"
  device_class: power
  icon: mdi:power-plug

- platform: mqtt
  state_topic: "slm/measurements"
  name: "Active power (total)"
  unique_id: "smappee_total_active_power"
  unit_of_measurement: "W"
  value_template: "{{value_json.total_activePower}}"
  device_class: power
  icon: mdi:power-plug

```

#### Controlling devices
```yaml
switch smappe_switch1:
  platform: mqtt
  command_topic: 'hass/smappee/control'
  payload_on: '{"id": 1, "status": true}'
  payload_off: '{"id": 1, "status": false}'
```
