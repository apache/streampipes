package de.fzi.proasense.demonstrator.adapter.festo;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONObject;

import de.fzi.proasense.demonstrator.adapter.UtilsValue;

public class PressuretankStream extends FestoSensorValue {
	private double ai_B103;

	@Override
	public void updateValues(Map<String, String> map) {
		ai_B103 = Value.getAI_B103(map);
	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("timestamp", System.currentTimeMillis());
		object.put("pressure", UtilsValue.round(ai_B103, 2).doubleValue());

		return object.toString();
	}
}

