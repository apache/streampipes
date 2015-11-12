package de.fzi.cep.sepa.esper.classification.number;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class NumberClassificationParameters extends BindingParameters {

	private String propertyName;
	private List<DataClassification> domainConceptData;

	public NumberClassificationParameters(SepaInvocation graph, String propertyName, List<DataClassification> domainConceptData) {
		super(graph);
		this.propertyName = propertyName;
		this.domainConceptData = domainConceptData;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public List<DataClassification> getDomainConceptData() {
		return domainConceptData;
	}
	
	
}
