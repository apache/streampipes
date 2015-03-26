package de.fzi.cep.sepa.esper.enrich.grid;

public class CellOption {
	private int cellX;
	private int cellY;
	
	private double latitudeNW;
	private double longitudeNW;
	private double latitudeSE;
	private double longitudeSE;
	
	private int cellSize;

	public CellOption(int cellX, int cellY, double latitudeNW,
			double longitudeNW, double latitudeSE, double longitudeSE,
			int cellSize) {
		super();
		this.cellX = cellX;
		this.cellY = cellY;
		this.latitudeNW = latitudeNW;
		this.longitudeNW = longitudeNW;
		this.latitudeSE = latitudeSE;
		this.longitudeSE = longitudeSE;
		this.cellSize = cellSize;
	}


	public int getCellX() {
		return cellX;
	}


	public void setCellX(int cellX) {
		this.cellX = cellX;
	}


	public int getCellY() {
		return cellY;
	}


	public void setCellY(int cellY) {
		this.cellY = cellY;
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

	public double getLatitudeSE() {
		return latitudeSE;
	}

	public void setLatitudeSE(double latitudeSE) {
		this.latitudeSE = latitudeSE;
	}

	public double getLongitudeSE() {
		return longitudeSE;
	}

	public void setLongitudeSE(double longitudeSE) {
		this.longitudeSE = longitudeSE;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}
}