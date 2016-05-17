package de.fzi.proasense.demonstrator.adapter.siemens;

import java.text.DecimalFormat;

import org.json.JSONObject;

import de.fzi.proasense.demonstrator.adapter.SensorValue;
import de.fzi.proasense.demonstrator.adapter.UtilsValue;

public class LevelSensorValue extends SensorValue {
	private float level;
	private float space;
	private float distance;
	private float volume;
	private float head;
	private float flow;
	private float temperature;
	private String hex;
	
	
	public LevelSensorValue(String hex) {
		super();
		hex = hex.replaceAll("\n", "");
		this.level = UtilsValue.getFourValue(hex, 0);
		this.space = UtilsValue.getFourValue(hex, 8);
		this.distance = UtilsValue.getFourValue(hex, 16);
		this.volume = UtilsValue.getFourValue(hex, 24);
		this.head = UtilsValue.getFourValue(hex, 32);
		this.flow = UtilsValue.getFourValue(hex, 40);
		this.temperature = UtilsValue.getFourValue(hex, 48);

	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("level", UtilsValue.round(level, 2).doubleValue());
		object.put("space", UtilsValue.round(space, 2).doubleValue());
		object.put("distance", UtilsValue.round(distance, 2).doubleValue());
		object.put("volume", UtilsValue.round(volume, 2).doubleValue());
		object.put("head", UtilsValue.round(head, 2).doubleValue());
		object.put("flow", UtilsValue.round(flow, 2).doubleValue());
		object.put("temperature", UtilsValue.round(temperature, 2).doubleValue());
		object.put("timestamp", System.currentTimeMillis());

		return object.toString();
	}
	
	
	public float getLevel() {
		return level;
	}
	public void setLevel(float level) {
		this.level = level;
	}
	public float getSpace() {
		return space;
	}
	public void setSpace(float space) {
		this.space = space;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getVolume() {
		return volume;
	}
	public void setVolume(float volume) {
		this.volume = volume;
	}
	public float getHead() {
		return head;
	}
	public void setHead(float head) {
		this.head = head;
	}
	public float getFlow() {
		return flow;
	}
	public void setFlow(float flow) {
		this.flow = flow;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	
	
}
