package org.streampipes.manager.generation;

import java.io.File;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.graph.DataProcessorDescription;

public class CodeGenerationManager {

	private DeploymentConfiguration config;
	private DataProcessorDescription element;
	
	public CodeGenerationManager(DeploymentConfiguration config, DataProcessorDescription element)
	{
		this.config = config;
		this.element = element;
	}

	public File getGeneratedFile()
	{
		// TODO check for config.getDeploymentType()
//		return new FlinkSepaCodeGenerator(config, element).createProject();
		return null;
	}
}
