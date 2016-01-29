package semantic_epa_sources_demonstrator.sources_demonstrator;

import java.text.DecimalFormat;

import org.json.JSONObject;

public class FlowRateSensorValue {
	private float massFlow;
	private float volumeFlow;
	private float density;
	private float fluidTemperature;
	private float sensorFaultFlags;
	private String hex;

	public FlowRateSensorValue() {

	}

	public FlowRateSensorValue(String hex) {
		this.hex = hex;
		this.massFlow = getFourValue(6);
		this.volumeFlow = getFourValue(14);
		this.density = getFourValue(22);
		this.fluidTemperature = getFourValue(47);
		this.sensorFaultFlags = getOneValue(76);
	}

	/**
	 * Converts the hexadecimal number from the String s to the Float value
	 * When there is a problem with the convertion Float.MIN_VALUE is returned so check for that
	 */
	public float hexToDouble(String s) {
		float j = Float.MIN_VALUE;

		try {
			int i = Integer.decode("0x" + s);
			j = Float.intBitsToFloat(i);
		} catch (NumberFormatException e) {
			System.out.println("Couldn't transform: " + s);
		}

		return j;
	}
	
	public String getFourByteString(int offset) {
		return getByteString(offset, 8);
	}

	public String getOneByteString(int offset) {
		return getByteString(offset, 2);
	}


	public String getByteString(int offset, int size) {
		if (offset + size <= hex.length()) {
			return hex.substring(offset, offset+size);
		} else {
			return "";
		}
	}
	
	private float getFourValue(int offset) {
		return hexToDouble(getFourByteString(offset));
	}

	private float getOneValue(int offset) {
		return hexToDouble(getOneByteString(offset));
	}
	
	public String toJson() {
		DecimalFormat df = new DecimalFormat("#.#######");
		JSONObject object = new JSONObject();
		object.put("massFlow", massFlow == Float.MIN_VALUE ? null : df.format(massFlow));
		object.put("volumeFlow", volumeFlow == Float.MIN_VALUE ? null : df.format(volumeFlow));
		object.put("density", density == Float.MIN_VALUE ? null : df.format(density));
		object.put("fluidTemperature", fluidTemperature == Float.MIN_VALUE ? null : df.format(fluidTemperature));
		object.put("sensorFaultFlags", sensorFaultFlags == Float.MIN_VALUE ? null : df.format(sensorFaultFlags));

		return object.toString();
	}

	public float getMassFlow() {
		return massFlow;
	}

	public void setMassFlow(float massFlow) {
		this.massFlow = massFlow;
	}

	public float getVolumeFlow() {
		return volumeFlow;
	}

	public void setVolumeFlow(float volumeFlow) {
		this.volumeFlow = volumeFlow;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public float getFluidTemperature() {
		return fluidTemperature;
	}

	public void setFluidTemperature(float fluidTemperature) {
		this.fluidTemperature = fluidTemperature;
	}

	public float getSensorFaultFlags() {
		return sensorFaultFlags;
	}

	public void setSensorFaultFlags(float sensorFaultFlags) {
		this.sensorFaultFlags = sensorFaultFlags;
	}

}
