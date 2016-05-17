package de.fzi.proasense.demonstrator.adapter.festo;

import java.util.Map;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.proasense.demonstrator.adapter.Main;
import de.fzi.proasense.demonstrator.adapter.SensorValue;

public class FestoProducer {

	private ProaSenseInternalProducer producer;
	private FestoSensorValue sensorValue;

	public FestoProducer(String broker, String topic, FestoSensorValue sensorValue) {
		this.producer = new ProaSenseInternalProducer(broker, topic);
		this.sensorValue = sensorValue;
	}
	
	public void sendToBroker(Map<String, String> map) {
		sensorValue.updateValues(map);
		producer.send(sensorValue.toJson().getBytes());
	}

}
