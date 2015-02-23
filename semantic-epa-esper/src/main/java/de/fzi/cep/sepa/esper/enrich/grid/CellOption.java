package de.fzi.cep.sepa.esper.enrich.grid;

public class CellOption {
	private int cellIdX;
	private int cellIdY;
	
	private double latitudeNW;
	private double longitudeNW;
	private double latitudeSW;
	private double longitudeSW;
	
	private int cellSize;

	public CellOption(int cellIdX, int cellIdY, double latitudeNW,
			double longitudeNW, double latitudeSW, double longitudeSW,
			int cellSize) {
		super();
		this.cellIdX = cellIdX;
		this.cellIdY = cellIdY;
		this.latitudeNW = latitudeNW;
		this.longitudeNW = longitudeNW;
		this.latitudeSW = latitudeSW;
		this.longitudeSW = longitudeSW;
		this.cellSize = cellSize;
	}

	public int getCellIdX() {
		return cellIdX;
	}

	public void setCellIdX(int cellIdX) {
		this.cellIdX = cellIdX;
	}

	public int getCellIdY() {
		return cellIdY;
	}

	public void setCellIdY(int cellIdY) {
		this.cellIdY = cellIdY;
	}

	public double getLatitudeNW() {
		return latitudeNW;
	}

	public void setLatitudeNW(double latitudeNW) {
		this.latitudeNW = latitudeNW;
	}

	public double getLongitudeNW() {
		return longitudeNW;
	}

	public void setLongitudeNW(double longitudeNW) {
		this.longitudeNW = longitudeNW;
	}

	public double getLatitudeSW() {
		return latitudeSW;
	}

	public void setLatitudeSW(double latitudeSW) {
		this.latitudeSW = latitudeSW;
	}

	public double getLongitudeSW() {
		return longitudeSW;
	}

	public void setLongitudeSW(double longitudeSW) {
		this.longitudeSW = longitudeSW;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}
}