package de.fzi.cep.sepa.messages;

public enum NotificationType {
	STORAGE_SUCCESS("Success", "Entity successfully stored"), 
	URIOFFLINE("URI not available", "Content could not be retrieved."), 
	NOSEPAFORMAT("Wrong Format", "Entity could not be recognized."), 
	UNKNOWN_ERROR("Unknown Error", "An unforeseen error has occurred."), 
	STORAGE_ERROR("Storage Error", "Entity could not be stored."),
	PIPELINE_STORAGE_SUCCESS("Success", "Pipeline stored successfully"), 
	PIPELINE_START_SUCCESS("Started", "Pipeline successfully started"), 
	PIPELINE_STOP_SUCCESS("Stopped", "Pipeline stopped successfully"),
	NO_VALID_CONNECTION("Not a valid connection", "Expected input event type does not match computed output type"),
	NO_SEPA_FOUND("No element found", "Could not find any element that matches the output of this element.");

	private final String title;
	private final String description;
	
	NotificationType(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String title()
	{
		return title;
	}
	
	public String description()
	{
		return description;
	}
	
	public Notification uiNotification()
	{
		return new Notification(title, description);
	}
}
