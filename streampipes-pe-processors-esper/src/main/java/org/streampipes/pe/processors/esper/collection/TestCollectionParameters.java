package org.streampipes.pe.processors.esper.collection;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

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
