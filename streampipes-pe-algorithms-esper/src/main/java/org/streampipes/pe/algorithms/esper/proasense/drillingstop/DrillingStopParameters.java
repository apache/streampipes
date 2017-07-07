package org.streampipes.pe.algorithms.esper.proasense.drillingstop;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class DrillingStopParameters extends BindingParameters {

	private int minRpm;
	private int minTorque;
	
	private String rpmPropertyName;
	private String torquePropertyName;
	
	public DrillingStopParameters(SepaInvocation graph, int minRpm, int minTorque, String rpmPropertyName, String torquePropertyName) {
		super(graph);
		this.minRpm = minRpm;
		this.minTorque = minTorque;
		this.rpmPropertyName = rpmPropertyName;
		this.torquePropertyName = torquePropertyName;
		
	}

	public int getMinRpm() {
		return minRpm;
	}

	public void setMinRpm(int minRpm) {
		this.minRpm = minRpm;
	}

	public int getMinTorque() {
		return minTorque;
	}

	public void setMinTorque(int minTorque) {
		this.minTorque = minTorque;
	}

	public String getRpmPropertyName() {
		return rpmPropertyName;
	}

	public void setRpmPropertyName(String rpmPropertyName) {
		this.rpmPropertyName = rpmPropertyName;
	}

	public String getTorquePropertyName() {
		return torquePropertyName;
	}

	public void setTorquePropertyName(String torquePropertyName) {
		this.torquePropertyName = torquePropertyName;
	}

	
	
}
