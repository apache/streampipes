package de.fzi.proasense.demonstrator.adapter.festo;

import java.text.DecimalFormat;
import java.util.Map;

import org.json.JSONObject;

public class ContainerB101Stream extends FestoSensorValue {

	private boolean di_B113;
	private boolean di_B114;

	@Override
	public void updateValues(Map<String, String> map) {
		di_B113 = Value.getDI_B113(map);
		di_B114 = Value.getDI_B114(map);
	}

	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("timestamp", System.currentTimeMillis());
		object.put("overflow", di_B114);
		object.put("underflow", di_B113);

		return object.toString();
	}

}
