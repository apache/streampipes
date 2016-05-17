package de.fzi.proasense.demonstrator.adapter.festo;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONObject;

import de.fzi.proasense.demonstrator.adapter.UtilsValue;

public class FestoFlowStream extends FestoSensorValue {
	private double ai_B102;
	private double ai_B104;

	@Override
	public void updateValues(Map<String, String> map) {
		ai_B102 = Value.getAI_B102(map);
		ai_B104 = Value.getAI_B104(map);
	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("timestamp", System.currentTimeMillis());
		object.put("mass_flow", UtilsValue.round(ai_B102, 2).doubleValue());
		object.put("temperature", UtilsValue.round(ai_B104, 2).doubleValue());

		return object.toString();
	}
}
