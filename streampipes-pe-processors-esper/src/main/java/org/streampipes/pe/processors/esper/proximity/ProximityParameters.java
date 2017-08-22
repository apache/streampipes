package org.streampipes.pe.processors.esper.proximity;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class ProximityParameters extends BindingParameters {

	private List<Location> proximityLocations;
	private double distance;
	
	private String latProperty;
	private String lngProperty;
	
	
	public ProximityParameters(SepaInvocation graph, List<Location> proximityLocations, double distance, String latProperty, String lngProperty) {
		super(graph);
		this.proximityLocations = proximityLocations;
		this.distance = distance;
		this.latProperty = latProperty;
		this.lngProperty = lngProperty;
	}


	public List<Location> getProximityLocations() {
		return proximityLocations;
	}


	public double getDistance() {
		return distance;
	}


	public String getLatProperty() {
		return latProperty;
	}


	public String getLngProperty() {
		return lngProperty;
	}
	
	

}
