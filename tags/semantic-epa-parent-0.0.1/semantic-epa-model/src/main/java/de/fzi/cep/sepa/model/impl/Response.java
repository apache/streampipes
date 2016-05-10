package de.fzi.cep.sepa.model.impl;

public class Response {

	private boolean success;
	
	private String elementId;
	
	private String optionalMessage;
	
	public Response(String elementId, boolean success)
	{
		this.elementId = elementId;
		this.success = success;
	}
	
	public Response(String elementId, boolean success, String optionalMessage)
	{
		this.elementId = elementId;
		this.success = success;
		this.optionalMessage = optionalMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getElementId() {
		return elementId;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}
	
}
