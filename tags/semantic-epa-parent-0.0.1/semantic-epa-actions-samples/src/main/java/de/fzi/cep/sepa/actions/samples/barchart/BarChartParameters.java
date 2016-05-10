package de.fzi.cep.sepa.actions.samples.barchart;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class BarChartParameters extends ActionParameters {

	private String keyName;
	private String valueName;
	private String listPropertyName;
	
	public BarChartParameters(String topic, String url, String listPropertyName, String keyName, String valueName) {
		super(topic, url);
		this.keyName = keyName;
		this.valueName = valueName;
		this.listPropertyName = listPropertyName;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getValueName() {
		return valueName;
	}
	
	public String getListPropertyName()
	{
		return listPropertyName;
	}
}
