package org.streampipes.model.client.matching;

public enum MatchingResultType {
	
	DATATYPE_MATCH("Datatype Match", "A required datatype is not present in the input event property."),
	STREAM_MATCH("Stream Match", ""), 
	DOMAIN_PROPERTY_MATCH("Domain Property Match", "A required domain property is not present in the input event property."), 
	FORMAT_MATCH("Format Match", "No supported transport format found."), 
	GROUNDING_MATCH("Grounding Match", ""), 
	PROTOCOL_MATCH("Protocol Match", "No supported communication protocol found."), 
	PROPERTY_MATCH("Property Match", "A required property is not present in the input event schema."), 
	SCHEMA_MATCH("Schema Match", "A required schema is not present in the input event stream."), 
	MEASUREMENT_UNIT_MATCH("Measurement Unit Match", ""), 
	STREAM_QUALITY("Stream Quality Match", "");
	
	private String title;
	private String description;
	
	private MatchingResultType(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
}
