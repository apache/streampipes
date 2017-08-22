package org.streampipes.wrapper.standalone.routing;


public abstract class Route {

	protected String routeId;
	protected String topic;
	
	public Route(String routeId, String topic) {
		this.routeId = routeId;
		this.topic = topic;
	}

	public String getRouteId() {
		return routeId;
	}

	public String getTopic() {
		return topic;
	}

	public abstract void startRoute();
	
	public abstract void stopRoute();
}
