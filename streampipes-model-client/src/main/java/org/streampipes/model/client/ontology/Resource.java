package org.streampipes.model.client.ontology;

public class Resource {

	private ElementType elementType;
	private String namespace;
	private String elementName;
	
	private String instanceOf;
	
	
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	public ElementType getElementType() {
		return elementType;
	}
	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}
	public String getInstanceOf() {
		return instanceOf;
	}
	public void setInstanceOf(String instanceOf) {
		this.instanceOf = instanceOf;
	}
}
