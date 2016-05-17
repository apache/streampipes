package de.fzi.proasense.demonstrator.adapter.siemens;

import de.fzi.proasense.demonstrator.adapter.SensorValue;

//0103203900000034094000446ea01a00000001858f507c41d14fdc0000000bff0000006ecc 00

public class FlowRateSensor extends SiemensSensor {
	
	public FlowRateSensor(String url, String topic) {
		super(url, topic);
	}

	@Override
	public SensorValue getSensorValues(String data) {
		return new FlowRateSensorValue(data);
	}
	
	
	

}
