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
	NO_SEPA_FOUND("No element found", "Could not find any element that matches the output of this element."), 
	NO_MATCHING_FORMAT_CONNECTION("Not a valid connection", "No supported input format matches produced output format"),
	NO_MATCHING_PROTOCOL_CONNECTION("Not a valid connection", "No supported input protocol matches provided output protocol"),
	LOGIN_FAILED("Login failed", "Please re-enter your password"),
	LOGIN_SUCCESS("Login success", ""),
	REGISTRATION_FAILED("Registration failed", "Please re-enter your password"),
	REGISTRATION_SUCCESS("Registered user successfully", ""),
	ALREADY_LOGGED_IN("User already logged in", ""),
	NOT_LOGGED_IN("User not logged in", ""),
	LOGOUT_SUCCESS("Successfully logged out", ""),
	
	VIRTUAL_SENSOR_STORAGE_SUCCESS("Success", "Pipeline block stored successfully"),
	
	PARSE_ERROR("Parse Exception", "Could not parse element description"),
	WARNING_NO_ICON("Icon missing", ""),
	WARNING_NO_NAME("Name missing", ""),
	WARNING_NO_LABEL("Description missing", ""),

	NOT_REMEMBERED("User not remembered", ""),
	REMEMBERED("User remembered", ""),

	NOT_REMOVED("Could not remove element", ""),
	REMOVED_ACTION("Action removed", ""),
	REMOVED_SOURCE("Source removed", ""),
	REMOVED_SEPA("Sepa removed", "");




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
