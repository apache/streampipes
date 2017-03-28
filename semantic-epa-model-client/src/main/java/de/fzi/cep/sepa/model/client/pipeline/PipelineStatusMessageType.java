package de.fzi.cep.sepa.model.client.pipeline;

public enum PipelineStatusMessageType {

	PIPELINE_STARTED("Pipeline started", "Pipeline successfully started."),
	PIPELINE_STOPPED("Pipeline stopped", "Pipeline successfully stopped."),
	
	PIPELINE_NO_DATA("No data arriving", "The input stream did not produce any data."),
	PIPELINE_EXCHANGE_SUCCESS("Stream exchanged", "The input stream was replaced with the backup sensor."),
	PIPELINE_EXCHANGE_FAILURE("Could not exchange stream", "We could not find any backup sensor.");
	
	private String title;
	private String description;
	
	PipelineStatusMessageType(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String title() {
		return title;
	}
	
	public String description() {
		return description;
	}
	
}
