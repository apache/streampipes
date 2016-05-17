package de.fzi.proasense.demonstrator.adapter.festo;

import java.util.Map;

import de.fzi.proasense.demonstrator.adapter.SensorValue;

public abstract class FestoSensorValue extends  SensorValue {
	public abstract void updateValues(Map<String, String> map);

}
