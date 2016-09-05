package de.fzi.cep.sepa.model.client.pipeline;

public class PipelineModificationErrorDescription {

	private String errorMessage;
	
	
	public PipelineModificationErrorDescription(String errorMessage)
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
