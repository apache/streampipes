package org.streampipes.model;

public enum DataSinkType {

	VISUALIZATION_CHART("Charts", ""),
	VISUALIZATION_GEO("Geospatial Visualization", ""),
	STORAGE("Storage", ""),
	FORWARD("Forward", ""),
	NOTIFICATION("Notifications", ""),
	ACTUATOR("Actuators", ""),
	UNCATEGORIZED("Uncategorized", "");
	
	private String label;
	private String description;
	
	DataSinkType(String label, String description) {
		this.label = label;
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}
}
