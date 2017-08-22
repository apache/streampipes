package org.streampipes.model.client.messages;

public class Notification {

	private String title;
	private String description;
	private String additionalInformation;
	
	public Notification(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}
	
	public Notification(String title, String description,
			String additionalInformation) {
		super();
		this.title = title;
		this.description = description;
		this.additionalInformation = additionalInformation;
	}



	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	
	
	
	
}
