package de.fzi.cep.sepa.runtime.flat.routing;

import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;

public abstract class Route {

	protected String routeId;
	protected DatatypeDefinition dataType;
	protected String topic;
	
	public Route(String routeId, DatatypeDefinition dataType, String topic) {
		this.routeId = routeId;
		this.dataType = dataType;
		this.topic = topic;
	}

	public String getRouteId() {
		return routeId;
	}

	public DatatypeDefinition getDataType() {
		return dataType;
	}

	public String getTopic() {
		return topic;
	}

	public abstract void startRoute();
	
	public abstract void stopRoute();
}
