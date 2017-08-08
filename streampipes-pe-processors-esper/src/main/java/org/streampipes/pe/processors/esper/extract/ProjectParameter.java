package org.streampipes.pe.processors.esper.extract;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

public class ProjectParameter extends BindingParameters{

	private List<NestedPropertyMapping> projectProperties;	
	
	public ProjectParameter(SepaInvocation graph, List<NestedPropertyMapping> projectProperties) {
		super(graph);
		this.projectProperties = projectProperties;
	}

	public List<NestedPropertyMapping> getProjectProperties() {
		return projectProperties;
	}

	public void setProjectProperties(List<NestedPropertyMapping> projectProperties) {
		this.projectProperties = projectProperties;
	}
	
}
