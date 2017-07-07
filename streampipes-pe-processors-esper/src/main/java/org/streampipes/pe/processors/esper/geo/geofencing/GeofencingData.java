package org.streampipes.pe.processors.esper.geo.geofencing;

public class GeofencingData {

	private double latitude;
	private double longitude;
	
	private int radius;
	
	public GeofencingData(double latitude, double longitude, int radius) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	
}
