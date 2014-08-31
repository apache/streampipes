package de.fzi.cep.sepa.rest.messages;

public enum NotificationType {
	STORAGE_SUCCESS("Success", "Entity successfully stored"), 
	URIOFFLINE("URI not available", "Content could not be retrieved."), 
	NOSEPAFORMAT("Wrong Format", "Entity could not be recognized."), 
	UNKNOWN_ERROR("Unknown Error", "An unforeseen error has occurred."), 
	STORAGE_ERROR("Storage Error", "Entity could not be stored.");

	private final String title;
	private final String description;
	
	NotificationType(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public Notification uiNotification()
	{
		return new Notification(title, description);
	}
}
