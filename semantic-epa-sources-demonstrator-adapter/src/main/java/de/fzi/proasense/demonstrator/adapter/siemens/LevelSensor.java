package de.fzi.proasense.demonstrator.adapter.siemens;

import de.fzi.proasense.demonstrator.adapter.SensorValue;

public class LevelSensor extends SiemensSensor {
	
	public LevelSensor(String url, String topic) {
		super(url, topic);
	}

	@Override
	public SensorValue getSensorValues(String data) {
		return new LevelSensorValue(data);
	}
	
	
	

}
