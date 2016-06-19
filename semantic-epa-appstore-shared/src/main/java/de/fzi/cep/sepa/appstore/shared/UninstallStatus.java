package de.fzi.cep.sepa.appstore.shared;

public class UninstallStatus {
	
	private boolean success;
	private String errorMessage;
	
	public UninstallStatus() {
		
	}
	
	public UninstallStatus(boolean success) {
		this.success = success;
	}
	
	public UninstallStatus(boolean success, String errorMessage) {
		super();
		this.success = success;
		this.errorMessage = errorMessage;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
