package de.fzi.cep.sepa.model.client.input;

public class PropertyMapping {

	private SelectFormInput input;
	private String elementId;
	
	private String runtimeName;
	private String runtimeType;
	private String domainProperty;
	
	private boolean renameAllowed;
	private boolean typeCastAllowed;
	private boolean domainPropertyCastAllowed;
		
	public PropertyMapping(SelectFormInput input, String runtimeName,
			String runtimeType, String domainProperty) {
		super();
		this.input = input;
		this.runtimeName = runtimeName;
		this.runtimeType = runtimeType;
		this.domainProperty = domainProperty;
	}
	public SelectInput getInput() {
		return input;
	}
	public void setInput(SelectFormInput input) {
		this.input = input;
	}
	public String getRuntimeName() {
		return runtimeName;
	}
	public void setRuntimeName(String runtimeName) {
		this.runtimeName = runtimeName;
	}
	public String getRuntimeType() {
		return runtimeType;
	}
	public void setRuntimeType(String runtimeType) {
		this.runtimeType = runtimeType;
	}
	public String getDomainProperty() {
		return domainProperty;
	}
	public void setDomainProperty(String domainProperty) {
		this.domainProperty = domainProperty;
	}
	public boolean isRenameAllowed() {
		return renameAllowed;
	}
	public void setRenameAllowed(boolean renameAllowed) {
		this.renameAllowed = renameAllowed;
	}
	public boolean isTypeCastAllowed() {
		return typeCastAllowed;
	}
	public void setTypeCastAllowed(boolean typeCastAllowed) {
		this.typeCastAllowed = typeCastAllowed;
	}
	public boolean isDomainPropertyCastAllowed() {
		return domainPropertyCastAllowed;
	}
	public void setDomainPropertyCastAllowed(boolean domainPropertyCastAllowed) {
		this.domainPropertyCastAllowed = domainPropertyCastAllowed;
	}
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	
	
	
}
