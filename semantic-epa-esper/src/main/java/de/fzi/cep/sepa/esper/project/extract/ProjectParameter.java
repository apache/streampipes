package de.fzi.cep.sepa.esper.project.extract;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class ProjectParameter extends BindingParameters{

	private List<NestedPropertyMapping> projectProperties;	
	
	public ProjectParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, List<NestedPropertyMapping> projectProperties) {
		super(inName, outName, allProperties, partitionProperties);
		this.projectProperties = projectProperties;
	}

	public List<NestedPropertyMapping> getProjectProperties() {
		return projectProperties;
	}

	public void setProjectProperties(List<NestedPropertyMapping> projectProperties) {
		this.projectProperties = projectProperties;
	}
	
}
