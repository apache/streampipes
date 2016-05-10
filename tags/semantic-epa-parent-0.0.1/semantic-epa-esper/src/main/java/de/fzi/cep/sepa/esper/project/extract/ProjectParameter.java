package de.fzi.cep.sepa.esper.project.extract;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
