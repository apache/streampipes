package de.fzi.cep.sepa.actions.samples.heatmap;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class HeatmapParameters extends ActionParameters {

	private String latitudeName;
	private String longitudeName;
	private int maxPoints;
	
	public HeatmapParameters(String topic, String url, String latitudeName, String longitudeName, int maxPoints) {
		super(topic, url);
		this.latitudeName = latitudeName;
		this.longitudeName = longitudeName;
		this.maxPoints = maxPoints;
	}

	public String getLatitudeName() {
		return latitudeName;
	}

	public void setLatitudeName(String latitudeName) {
		this.latitudeName = latitudeName;
	}

	public String getLongitudeName() {
		return longitudeName;
	}

	public void setLongitudeName(String longitudeName) {
		this.longitudeName = longitudeName;
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}	
}
