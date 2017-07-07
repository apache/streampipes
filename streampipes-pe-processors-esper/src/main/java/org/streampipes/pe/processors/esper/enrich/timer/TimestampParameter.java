package org.streampipes.pe.processors.esper.enrich.timer;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class TimestampParameter extends BindingParameters {

	private List<String> propertyNames;
	private String appendTimePropertyName;
	
	public TimestampParameter(SepaInvocation graph, String appendTimePropertyName, List<String> propertyNames) {
		super(graph);
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
