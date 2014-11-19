package de.fzi.cep.sepa.messages;

import java.util.List;

import de.fzi.cep.sepa.model.client.ErrorDescription;
import de.fzi.cep.sepa.model.client.StaticProperty;

public class PipelineModification {

	String domId;
	String elementId;
	
	List<ErrorDescription> errorDescriptions;
	List<StaticProperty> staticProperties;
	
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
	
	
}
