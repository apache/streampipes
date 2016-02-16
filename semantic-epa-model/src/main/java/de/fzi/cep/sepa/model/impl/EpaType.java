package de.fzi.cep.sepa.model.impl;


public enum EpaType {

	FILTER("a", "b"),
	AGGREGATE("", ""),
	ENRICH_TEXT("", ""),
	GEO("", ""),
	PATTERN_DETECT("", ""),
	ALGORITHM("", ""),
	TRANSFORM("", "");
	
	private String label;
	private String description;
	
	EpaType(String label, String description) {
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
