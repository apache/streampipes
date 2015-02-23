package de.fzi.cep.sepa.esper.enrich.grid;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class GridEnrichmentParameter extends BindingParameters {

	private double latitudeStart;
	private double longitudeStart;
	
	private int cellSize;
	
	private String cellOptionsPropertyName;
	
	private String latPropertyName;
	private String lngPropertyName;
	private List<String> selectProperties;
	
	public GridEnrichmentParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, double latitudeStart, double longitudeStart, int cellSize, String cellOptionsPropertyName, String latPropertyName, String lngPropertyName, List<String> selectProperties) {
		super(inName, outName, allProperties, partitionProperties);
		this.latitudeStart = latitudeStart;
		this.longitudeStart = longitudeStart;
		this.cellSize = cellSize;
		this.cellOptionsPropertyName = cellOptionsPropertyName;
		this.selectProperties = selectProperties;
		this.latPropertyName = latPropertyName;
		this.lngPropertyName = lngPropertyName;
	}

	public double getLatitudeStart() {
		return latitudeStart;
	}

	public void setLatitudeStart(double latitudeStart) {
		this.latitudeStart = latitudeStart;
	}

	public double getLongitudeStart() {
		return longitudeStart;
	}

	public void setLongitudeStart(double longitudeStart) {
		this.longitudeStart = longitudeStart;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public String getCellOptionsPropertyName() {
		return cellOptionsPropertyName;
	}

	public void setCellOptionsPropertyName(String cellOptionsPropertyName) {
		this.cellOptionsPropertyName = cellOptionsPropertyName;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

	public void setSelectProperties(List<String> selectProperties) {
		this.selectProperties = selectProperties;
	}

	public String getLatPropertyName() {
		return latPropertyName;
	}

	public void setLatPropertyName(String latPropertyName) {
		this.latPropertyName = latPropertyName;
	}

	public String getLngPropertyName() {
		return lngPropertyName;
	}

	public void setLngPropertyName(String lngPropertyName) {
		this.lngPropertyName = lngPropertyName;
	}
	
}
