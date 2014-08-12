package de.fzi.cep.sepa.model.impl;

import java.util.List;

public interface PipelineElement {

	public boolean hasPredecessor();
	
	public boolean hasSuccessor();
	
	public PipelineElement getOutputElement(PipelineElement element);
	
	public List<PipelineElement> getInputElements(PipelineElement element);
	
	public void setOutputElement(PipelineElement element, PipelineElement output);
	
	
		
}
