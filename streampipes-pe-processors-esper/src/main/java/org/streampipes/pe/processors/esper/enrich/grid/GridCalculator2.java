package org.streampipes.pe.processors.esper.enrich.grid;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class GridCalculator2 {

	private static double LAT = 41.474937;
    private static double LON = -74.913585;

    // 250 meter south corresponds to a change of 0.002245778
    private static double SOUTHDIFF = 0.002245778;

    // 500 meter east corresponds to a change of 0.002993 degrees
    private static double EASTDIFF = 0.002993;
	
	public CellOption computeCellsNaive(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart)
	{
		LatLng startLocation = new LatLng(latitudeStart, longitudeStart);
		
		int cellX = calculateXCoordinate(longitude);
	    int cellY = calculateYCoordinate(latitude);
         
        //LatLng nw = move(move(startLocation, 315, cellSize/2), 90, (cellX-1)*cellSize);
 		//LatLng se = move(move(startLocation, 135, cellSize/2), 90, (cellY-1)*cellSize);
 		
		return new CellOption(cellX, cellY, 0, 0, 0, 0, 250);
 		//return new CellOption(cellX, cellY, nw.getLatitude(), nw.getLongitude(), se.getLatitude(), se.getLongitude(), 250);
	}
	
	public CellOption computeCells(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart) {
		LatLng currentLocation = new LatLng(latitude, longitude);
		LatLng startLocation = new LatLng(latitudeStart, longitudeStart);
		
		double distance = distance(startLocation, currentLocation);
		int cellX = findMinimal(startLocation, currentLocation, 1, distance, 500, 270);
		int cellY = findMinimal(startLocation, currentLocation, 1, distance, 500, 0);
	
		LatLng nw = move(move(startLocation, 315, cellSize/2), 90, (cellX-1)*cellSize);
		LatLng se = move(move(startLocation, 135, cellSize/2), 90, (cellY-1)*cellSize);
		System.out.println("x= " +cellX +" y=" +cellY);
		/*
		System.out.println("nw lat " +nw.getLatitude());
		System.out.println("nw lng " +nw.getLongitude());
		*/
		return new CellOption(cellX, cellY, nw.getLatitude(), nw.getLongitude(), se.getLatitude(), se.getLongitude(), 500);
	}
	
	private static int calculateXCoordinate(double longitude) {
	        Double ret = (Math.abs(LON - longitude)/ EASTDIFF);
	        return ret.intValue();
	}
	 
    private static int calculateYCoordinate(double latitude) {
        Double ret = (Math.abs(LAT - latitude)/ SOUTHDIFF);
        return ret.intValue();
    }
    
    private boolean isValidLatitude(double lat) {
        return (lat <= LAT && lat > LAT - SOUTHDIFF*600);
    }

    private boolean isValidLongitude(double lon) {
        return (lon > LON && lon < LON + EASTDIFF*600);
    }
	
	private int findMinimal(LatLng start, LatLng current, int xValue, double currentDistance, int metersToWalk, double bearing)
	{
		LatLng newLocation = move(current, bearing, metersToWalk);
		double newDistance = distance(start, newLocation);
		if (newDistance < currentDistance) 
			{
				xValue += 1;
				return findMinimal(start, newLocation, xValue, newDistance, metersToWalk, bearing);
			}
		else return xValue;
	}
	
	private double distance(LatLng start, LatLng current)
	{
		return LatLngTool.distance(start, current, LengthUnit.METER);
	}
	
	private LatLng moveWest(LatLng start, double distance)
	{
		return move(start, 270, distance);
	}
	
	private LatLng moveEast(LatLng start, double distance)
	{
		return move(start, 90, distance);
	}
	
	private LatLng moveNorth(LatLng start, double distance)
	{
		return move(start, 0, distance);
	}
	
	private LatLng move(LatLng start, double bearing, double distance)
	{
		return LatLngTool.travel(start, bearing, distance, LengthUnit.METER);
	}
	
	private double findLatitude(int cellY) {
		// TODO Auto-generated method stub
		return 0;
	}
}
