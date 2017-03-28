package de.fzi.cep.sepa.model.client.pipeline;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

import java.util.ArrayList;
import java.util.List;

public class PipelineModification {

	String domId;
	String elementId;
	
	List<PipelineModificationErrorDescription> errorDescriptions;
	List<StaticProperty> staticProperties;
	List<OutputStrategy> outputStrategies;
	List<EventStream> inputStreams;
	
	public PipelineModification(String domId, String elementId,
			List<StaticProperty> staticProperties) {
		super();
		this.domId = domId;
		this.elementId = elementId;
		this.staticProperties = staticProperties;
		this.inputStreams = new ArrayList<>();
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

	public List<EventStream> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<EventStream> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public void addInputStream(EventStream inputStream) {
		this.inputStreams.add(inputStream);
	}
}
