package de.fzi.proasense.demonstrator.adapter.festo;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONObject;

import de.fzi.proasense.demonstrator.adapter.UtilsValue;

public class ContainerB102Stream extends FestoSensorValue {
	private boolean di_S112;
	private double ai_B101;

	@Override
	public void updateValues(Map<String, String> map) {
		di_S112 = Value.getDI_S112(map);
		ai_B101 = Value.getAI_B101(map);
	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("timestamp", System.currentTimeMillis());
		object.put("level", UtilsValue.round(ai_B101, 2).doubleValue());
		object.put("underflow", di_S112);

		return object.toString();
	}
}
