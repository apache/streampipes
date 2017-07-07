package de.fzi.cep.sepa.model.client.messages;

import java.util.List;

public class AppInstallationMessage {

	private boolean success;
	private String errorMessage;
	
	List<Message> importMessages;
		
	public AppInstallationMessage(boolean success, List<Message> importMessages) {
		super();
		this.success = success;
		this.importMessages = importMessages;
	}
	
	public AppInstallationMessage(boolean success, String errorMessage) {
		this.success = success;
		this.errorMessage = errorMessage;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Message> getImportMessages() {
		return importMessages;
	}
	public void setImportMessages(List<Message> importMessages) {
		this.importMessages = importMessages;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}	
	
}
