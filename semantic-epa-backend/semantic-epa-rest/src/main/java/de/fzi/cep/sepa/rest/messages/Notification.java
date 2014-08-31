package de.fzi.cep.sepa.rest.messages;

public class Notification {

	private String title;
	private String description;
	
	public Notification(String title, String description) {
		super();
		this.title = title;
		this.description = description;
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
	
	
}
