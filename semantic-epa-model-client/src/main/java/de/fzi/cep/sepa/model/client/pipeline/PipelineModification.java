package de.fzi.cep.sepa.model.client.pipeline;

import java.util.List;

import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationErrorDescription;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

public class PipelineModification {

	String domId;
	String elementId;
	
	List<PipelineModificationErrorDescription> errorDescriptions;
	List<StaticProperty> staticProperties;
	List<OutputStrategy> outputStrategies;
	
	public PipelineModification(String domId, String elementId,
			List<StaticProperty> staticProperties) {
		super();
		this.domId = domId;
		this.elementId = elementId;
		this.staticProperties = staticProperties;
	}
	
	public PipelineModification()
	{
		
	}

	public String getDomId() {
		return domId;
	}

	public void setDomId(String domId) {
		this.domId = domId;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public List<OutputStrategy> getOutputStrategies() {
		return outputStrategies;
	}

	public void setOutputStrategies(List<OutputStrategy> outputStrategies) {
		this.outputStrategies = outputStrategies;
	}
}
