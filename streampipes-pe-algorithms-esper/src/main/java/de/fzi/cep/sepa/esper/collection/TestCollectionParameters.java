package de.fzi.cep.sepa.esper.collection;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class TestCollectionParameters extends BindingParameters {

	private String propertyName;
	private List<DataRange> domainConceptData;
	
	public TestCollectionParameters(SepaInvocation graph, String propertyName, List<DataRange> domainConceptData) {
		super(graph);
		this.propertyName = propertyName;
		this.domainConceptData = domainConceptData;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public List<DataRange> getDomainConceptData() {
		return domainConceptData;
	}
	
	

}
