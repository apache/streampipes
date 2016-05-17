package de.fzi.cep.sepa.model.client;

public class ErrorDescription {

	private String errorMessage;
	
	
	public ErrorDescription(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
