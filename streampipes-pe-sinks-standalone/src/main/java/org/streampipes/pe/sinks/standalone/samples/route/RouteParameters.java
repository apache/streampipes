package org.streampipes.pe.sinks.standalone.samples.route;

import org.streampipes.pe.sinks.standalone.samples.ActionParameters;

public class RouteParameters extends ActionParameters {

	private String latitudeName;
	private String longitudeName;
	private String labelName;
	
	public RouteParameters(String topic, String url, String latitudeName, String longitudeName, String labelName) {
		super(topic, url);
		this.labelName = labelName;
		this.latitudeName = latitudeName;
		this.longitudeName = longitudeName;
	}

	public String getLatitudeName() {
		return latitudeName;
	}

	public String getLongitudeName() {
		return longitudeName;
	}

	public String getLabelName() {
		return labelName;
	}

}
