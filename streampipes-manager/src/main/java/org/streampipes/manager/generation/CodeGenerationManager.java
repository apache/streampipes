package org.streampipes.manager.generation;

import java.io.File;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.impl.graph.SepaDescription;

public class CodeGenerationManager {

	private DeploymentConfiguration config;
	private SepaDescription element;
	
	public CodeGenerationManager(DeploymentConfiguration config, SepaDescription element)
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
