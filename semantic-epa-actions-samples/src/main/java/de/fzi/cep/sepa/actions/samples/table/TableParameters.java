package de.fzi.cep.sepa.actions.samples.table;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class TableParameters extends ActionParameters {

	private int numberOfRows;
	private String[] columnNames;
	
	public TableParameters(String topic, String url, int numberOfRows, String[] columnNames) {
		super(topic, url);
		this.numberOfRows = numberOfRows;
		this.columnNames = columnNames;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	


}
