package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

import java.util.List;

public class ConfigurableTimestampParameters extends BindingParameters {

	private String appendTimePropertyName;
	private List<String> selectProperties;
	
	public ConfigurableTimestampParameters(SepaInvocation graph,
                                           String appendTimePropertyName, List<String> selectProperties) {
		super(graph);
		this.appendTimePropertyName = appendTimePropertyName;
		this.selectProperties = selectProperties;
	}

	public String getAppendTimePropertyName() {
		return appendTimePropertyName;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

}
