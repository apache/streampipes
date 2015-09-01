package de.fzi.cep.sepa.messages;

public class ProaSenseNotificationMessage {

	private String notificationId;
	private String title;
	private long timestamp;
	private String description;
	private boolean read;
	
	public ProaSenseNotificationMessage(String title, long timestamp, String description) {
		this.title = title;
		this.timestamp = timestamp;
		this.description = description;
		this.read = false;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
	
}
