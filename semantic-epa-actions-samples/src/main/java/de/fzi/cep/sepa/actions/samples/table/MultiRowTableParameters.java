package de.fzi.cep.sepa.actions.samples.table;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class MultiRowTableParameters extends ActionParameters {

	private String[] columnNames;
	private boolean replace;
	private String listProperty;
	
	public MultiRowTableParameters(String topic, String url, boolean replace, String listProperty, String[] columnNames) {
		super(topic, url);
		this.replace = replace;
		this.listProperty = listProperty;
		this.columnNames = columnNames;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	public String getListProperty() {
		return listProperty;
	}

	public void setListProperty(String listProperty) {
		this.listProperty = listProperty;
	}

}
