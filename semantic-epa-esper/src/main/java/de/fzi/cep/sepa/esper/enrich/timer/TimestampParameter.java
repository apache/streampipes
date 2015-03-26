package de.fzi.cep.sepa.esper.enrich.timer;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class TimestampParameter extends BindingParameters {

	private List<String> propertyNames;
	private String appendTimePropertyName;
	
	public TimestampParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, String appendTimePropertyName, List<String> propertyNames) {
		super(inName, outName, allProperties, partitionProperties);
		this.appendTimePropertyName = appendTimePropertyName;
		this.propertyNames = propertyNames;
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	public String getAppendTimePropertyName() {
		return appendTimePropertyName;
	}

	public void setAppendTimePropertyName(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}
	
}
