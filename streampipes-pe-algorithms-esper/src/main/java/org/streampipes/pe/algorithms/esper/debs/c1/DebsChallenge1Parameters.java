package org.streampipes.pe.algorithms.esper.debs.c1;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class DebsChallenge1Parameters extends BindingParameters {

	private double startingLatitude, startingLongitude;
	private int cellSize;
	private String latitudeName, longitudeName, latitude2Name, longitude2Name;
	private List<String> propertyNames;
	
	public DebsChallenge1Parameters(SepaInvocation graph, double startingLatitude, double startingLongitude, int cellSize, String latitudeName, String longitudeName, String latitude2Name, String longitude2Name, List<String> propertyNames) {
		super(graph);
		this.startingLatitude = startingLatitude;
		this.startingLongitude = startingLongitude;
		this.cellSize = cellSize;
		this.latitudeName = latitudeName;
		this.latitude2Name = latitude2Name;
		this.longitude2Name = longitude2Name;
		this.longitudeName = longitudeName;
		this.propertyNames = propertyNames;
	}

	public double getStartingLatitude() {
		return startingLatitude;
	}

	public void setStartingLatitude(double startingLatitude) {
		this.startingLatitude = startingLatitude;
	}

	public double getStartingLongitude() {
		return startingLongitude;
	}

	public void setStartingLongitude(double startingLongitude) {
		this.startingLongitude = startingLongitude;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public String getLatitudeName() {
		return latitudeName;
	}

	public void setLatitudeName(String latitudeName) {
		this.latitudeName = latitudeName;
	}

	public String getLongitudeName() {
		return longitudeName;
	}

	public void setLongitudeName(String longitudeName) {
		this.longitudeName = longitudeName;
	}

	public String getLatitude2Name() {
		return latitude2Name;
	}

	public void setLatitude2Name(String latitude2Name) {
		this.latitude2Name = latitude2Name;
	}

	public String getLongitude2Name() {
		return longitude2Name;
	}

	public void setLongitude2Name(String longitude2Name) {
		this.longitude2Name = longitude2Name;
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

}
