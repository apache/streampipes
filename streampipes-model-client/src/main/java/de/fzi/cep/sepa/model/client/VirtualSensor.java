package de.fzi.cep.sepa.model.client;

import de.fzi.cep.sepa.model.client.pipeline.ElementComposition;

public class VirtualSensor extends ElementComposition {

	protected String createdBy;
	
	protected int outputIndex;
	
	public VirtualSensor()
	{
		super();
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public int getOutputIndex() {
		return outputIndex;
	}

	public void setOutputIndex(int outputIndex) {
		this.outputIndex = outputIndex;
	}
	
	
	
}
