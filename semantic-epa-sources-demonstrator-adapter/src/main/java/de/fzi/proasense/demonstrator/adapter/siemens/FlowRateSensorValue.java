package de.fzi.proasense.demonstrator.adapter.siemens;

import java.text.DecimalFormat;

import org.json.JSONObject;
import org.openrdf.query.algebra.evaluation.function.numeric.Round;

import de.fzi.proasense.demonstrator.adapter.SensorValue;
import de.fzi.proasense.demonstrator.adapter.UtilsValue;

public class FlowRateSensorValue extends SensorValue {
	private float massFlow;
	private float volumeFlow;
	private float density;
	private float fluidTemperature;
	private float sensorFaultFlags;

	public FlowRateSensorValue() {

	}

	public FlowRateSensorValue(String hex) {
		hex = hex.replaceAll("\n", "");
		this.massFlow = UtilsValue.getFourValue(hex, 6);
		this.volumeFlow = UtilsValue.getFourValue(hex, 14) * 60000;
		this.density = UtilsValue.getFourValue(hex, 22);
		this.fluidTemperature = UtilsValue.getFourValue(hex, 46);
		this.sensorFaultFlags = UtilsValue.getOneValue(hex, 76);
	}


	@Override
	public String toJson() {
		JSONObject object = new JSONObject();

		object.put("mass_flow", UtilsValue.round(massFlow, 2).doubleValue());
		object.put("volume_flow", UtilsValue.round(volumeFlow, 2).doubleValue());
		object.put("density", UtilsValue.round(density, 2).doubleValue());
		object.put("fluid_temperature", UtilsValue.round(fluidTemperature, 2).doubleValue());
		object.put("sensor_fault_flags", UtilsValue.round(sensorFaultFlags,2).doubleValue());
		object.put("timestamp", System.currentTimeMillis());
		

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
