package de.fzi.cep.sepa.messages;

import org.apache.commons.lang.RandomStringUtils;

public class ProaSenseNotificationMessage {

	private String notificationId;
	private String targetedUser;
	private String title;
	private long timestamp;
	private String description;
	private boolean read;
	
	public ProaSenseNotificationMessage(String title, long creationDate, String description, String targetedUser) {
		this.notificationId = RandomStringUtils.randomAlphanumeric(10);
		this.targetedUser = targetedUser;
		this.title = title;
		this.timestamp = creationDate;
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

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getTargetedUser() {
		return targetedUser;
	}

	public void setTargetedUser(String targetedUser) {
		this.targetedUser = targetedUser;
	}
	
	
	
}
