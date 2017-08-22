package org.streampipes.model.impl;


public enum EpaType {

	FILTER("Filter", "b"),
	AGGREGATE("Aggregation", ""),
	ENRICH_TEXT("Text Functions", ""),
	ENRICH("Enrichment", ""),
	GEO("Geospatial Operations", ""),
	PATTERN_DETECT("Pattern Detection", ""),
	ALGORITHM("Algorithm", ""),
	TRANSFORM("Transformation", ""),
	UNCATEGORIZED("Uncategorized", "");
	
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
