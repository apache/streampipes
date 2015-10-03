package de.fzi.cep.sepa.kpi;

public class UnaryOperation extends Operation {

	protected UnaryOperationType unaryOperationType;

	private String sensorId;
	private String eventPropertyName;
	
	private String propertyRestriction;
	private String propertyType;
	
	private boolean partition;
	private String partitionProperty;
	
	private Window window;
	
	
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	public String getEventPropertyName() {
		return eventPropertyName;
	}
	public void setEventPropertyName(String eventPropertyName) {
		this.eventPropertyName = eventPropertyName;
	}
	public boolean isPartition() {
		return partition;
	}
	public void setPartition(boolean partition) {
		this.partition = partition;
	}
	public String getPartitionProperty() {
		return partitionProperty;
	}
	public void setPartitionProperty(String partitionProperty) {
		this.partitionProperty = partitionProperty;
	}
	
	public UnaryOperationType getUnaryOperationType() {
		return unaryOperationType;
	}

	public void setUnaryOperationType(UnaryOperationType unaryOperationType) {
		this.unaryOperationType = unaryOperationType;
	}
	public String getPropertyRestriction() {
		return propertyRestriction;
	}
	public void setPropertyRestriction(String propertyRestriction) {
		this.propertyRestriction = propertyRestriction;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public Window getWindow() {
		return window;
	}
	public void setWindow(Window window) {
		this.window = window;
	}
	
	
	
	
}
