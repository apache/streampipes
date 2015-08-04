package de.fzi.cep.sepa.messages;

public class PipelineElementStatus {

	private String elementId;
	private String optionalMessage;
	
	private boolean success;
	
	public PipelineElementStatus(String elementId, boolean success, String optionalMessage)
	{
		this.elementId = elementId;
		this.optionalMessage = optionalMessage;
		this.success = success;
	}

	public String getElementId() {
		return elementId;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}

	public boolean isSuccess() {
		return success;
	}
}
