package de.fzi.cep.sepa.esper.geo.geofencing;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class GeofencingParameters extends BindingParameters {

	private GeofencingOperation operation;
	private GeofencingData geofencingData;
	
	private String latitudeMapping;
	private String longitudeMapping;
	private String partitionMapping;

	public GeofencingParameters(SepaInvocation invocationGraph,
			GeofencingOperation operation, GeofencingData geofencingData, String latitudeMapping, String longitudeMapping, String partitionMapping) {
		super(invocationGraph);
		this.operation = operation;
		this.geofencingData = geofencingData;
		this.latitudeMapping = latitudeMapping;
		this.longitudeMapping = longitudeMapping;
		this.partitionMapping = partitionMapping;
	}


	public GeofencingOperation getOperation() {
		return operation;
	}


	public void setOperation(GeofencingOperation operation) {
		this.operation = operation;
	}


	public GeofencingData getGeofencingData() {
		return geofencingData;
	}


	public void setGeofencingData(GeofencingData geofencingData) {
		this.geofencingData = geofencingData;
	}


	public String getLatitudeMapping() {
		return latitudeMapping;
	}


	public String getLongitudeMapping() {
		return longitudeMapping;
	}


	public String getPartitionMapping() {
		return partitionMapping;
	}


	public void setPartitionMapping(String partitionMapping) {
		this.partitionMapping = partitionMapping;
	}
	
	
	
}
