package org.streampipes.pe.processors.esper.extract;

import java.util.List;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ProjectParameter extends EventProcessorBindingParams {

	private List<NestedPropertyMapping> projectProperties;	
	
	public ProjectParameter(DataProcessorInvocation graph, List<NestedPropertyMapping> projectProperties) {
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
