package de.fzi.cep.sepa.model.client.pipeline;

public class PipelineElementStatus {

	private String elementId;
	private String elementName;
	private String optionalMessage;
	
	private boolean success;
	
	public PipelineElementStatus(String elementId, String elementName, boolean success, String optionalMessage)
	{
		this.elementId = elementId;
		this.elementName = elementName;
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

	public String getElementName() {
		return elementName;
	}
	
	
}
