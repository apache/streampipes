package de.fzi.cep.sepa.actions.samples.maparealist;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class MapAreaParameters extends ActionParameters {
	
	private String latitudeNw;
	private String longitudeNw;
	private String latitudeSe;
	private String longitudeSe;
	private String labelName;
	
	public MapAreaParameters(String topic, String url, String latitudeNw, String longitudeNw, String latitudeSe, String longitudeSe, String labelName) {
		super(topic, url);
		this.latitudeNw = latitudeNw;
		this.longitudeNw = longitudeNw;
		this.latitudeSe = latitudeSe;
		this.longitudeSe = longitudeSe;
		this.labelName = labelName;
	}

	
	public String getLatitudeNw() {
		return latitudeNw;
	}


	public void setLatitudeNw(String latitudeNw) {
		this.latitudeNw = latitudeNw;
	}


	public String getLongitudeNw() {
		return longitudeNw;
	}


	public void setLongitudeNw(String longitudeNw) {
		this.longitudeNw = longitudeNw;
	}


	public String getLatitudeSe() {
		return latitudeSe;
	}


	public void setLatitudeSe(String latitudeSe) {
		this.latitudeSe = latitudeSe;
	}


	public String getLongitudeSe() {
		return longitudeSe;
	}


	public void setLongitudeSe(String longitudeSe) {
		this.longitudeSe = longitudeSe;
	}


	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	

}
