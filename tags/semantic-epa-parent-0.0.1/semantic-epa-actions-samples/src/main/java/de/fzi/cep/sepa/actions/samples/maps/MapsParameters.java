package de.fzi.cep.sepa.actions.samples.maps;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class MapsParameters extends ActionParameters {

	private String latitudeName;
	private String longitudeName;
	private String labelName;
	
	public MapsParameters(String topic, String url, String latitudeName, String longitudeName, String labelName) {
		super(topic, url);
		this.latitudeName = latitudeName;
		this.longitudeName = longitudeName;
		this.labelName = labelName;
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

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	
	

}
