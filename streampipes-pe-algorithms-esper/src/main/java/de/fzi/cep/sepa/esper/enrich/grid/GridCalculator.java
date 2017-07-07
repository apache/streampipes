package de.fzi.cep.sepa.esper.enrich.grid;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class GridCalculator {

	 private static final double LAT = 41.474937;
	 private static final double LON = -74.913585;
	 private double SOUTHDIFF = 0.004491556;
	 private double EASTDIFF = 0.005986;
	
	public CellOption computeCellsNaive(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart)
	{
		this.SOUTHDIFF = (cellSize/500.0)*SOUTHDIFF;
		this.EASTDIFF = (cellSize/500.0)*EASTDIFF;
		LatLng startLocation = new LatLng(latitudeStart, longitudeStart);
		
		int cellX = calculateXCoordinate(longitude);
	    int cellY = calculateYCoordinate(latitude);

//	    System.out.println(cellX);
//		System.out.println(cellY);

        //LatLng nw = move(move(startLocation, 315, cellSize/2), 90, (cellX-1)*cellSize);
	    //LatLng se = move(move(startLocation, 135, cellSize/2), 90, (cellY-1)*cellSize);
 		System.out.println("x=" +cellX+ " y=" +cellY);
 		//return new CellOption(cellX, cellY, 0, 0, 0, 0, 500);
 		//return new CellOption(cellX, cellY, nw.getLatitude(), nw.getLongitude(), se.getLatitude(), se.getLongitude(), cellSize);
 		return new CellOption(cellX, cellY, LAT-(cellY*SOUTHDIFF), LON+(cellX*EASTDIFF), LAT-((cellY+1)*SOUTHDIFF), LON + ((cellX+1)*EASTDIFF), cellSize);
 		

	}
	
	public CellOption computeCells(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart) {
		LatLng currentLocation = new LatLng(latitude, longitude);
		LatLng startLocation = new LatLng(latitudeStart, longitudeStart);
		
		double distance = distance(startLocation, currentLocation);
		int cellX = findMinimal(startLocation, currentLocation, 1, distance, 500, 270);
		int cellY = findMinimal(startLocation, currentLocation, 1, distance, 500, 0);
	
		LatLng nw = move(move(startLocation, 315, cellSize/2), 90, (cellX-1)*cellSize);
		LatLng se = move(move(startLocation, 135, cellSize/2), 90, (cellY-1)*cellSize);
		
//		System.out.println("nw lat " +nw.getLatitude());
//		System.out.println("nw lng " +nw.getLongitude());

		return new CellOption(cellX, cellY, nw.getLatitude(), nw.getLongitude(), se.getLatitude(), se.getLongitude(), 500);
	}
	
	private int calculateXCoordinate(double longitude) {
	        Double ret = (Math.abs(LON - longitude)/ EASTDIFF);
	        return ret.intValue() +1;
	}
	 
    private int calculateYCoordinate(double latitude) {
        Double ret = (Math.abs(LAT - latitude)/ SOUTHDIFF);
        return ret.intValue() +1;
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
	
	public static void main(String[] args)
	{
		double startLatitude = 41.474937;
		double startLongitude = -74.913585;
		LatLng start = new LatLng(startLatitude, startLongitude);
		
		double currentLatitude = 41.474937;
		double currentLongitude = -74.913585;
		
		GridCalculator calc = new GridCalculator();
		
		LatLng eastCoordinate = calc.moveEast(new LatLng(startLatitude, startLongitude), 2500);
		LatLng southCoordinate = calc.move(eastCoordinate, 180, 1000);
		System.out.println("lat: " +southCoordinate.getLatitude());
		System.out.println("lng: " +southCoordinate.getLongitude());
		
		calc.computeCells(currentLatitude, currentLongitude, 500, startLatitude, startLongitude);
	}

}
