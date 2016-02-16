package de.fzi.cep.sepa.model.impl;

public enum EcType {

	VISUALIZATION_CHART("", ""),
	VISUALIZATION_GEO("", ""),
	STORAGE("", ""),
	FORWARD("", ""),
	NOTIFICATION("", ""),
	ACTUATOR("", "");
	
	private String label;
	private String description;
	
	EcType(String label, String description) {
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
