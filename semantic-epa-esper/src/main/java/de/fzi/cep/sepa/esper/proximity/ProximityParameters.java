package de.fzi.cep.sepa.esper.proximity;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class ProximityParameters extends BindingParameters {

	private List<Location> proximityLocations;
	private double distance;
	
	private String latProperty;
	private String lngProperty;
	
	
	public ProximityParameters(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, List<Location> proximityLocations, double distance, String latProperty, String lngProperty) {
		super(inName, outName, allProperties, partitionProperties);
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
