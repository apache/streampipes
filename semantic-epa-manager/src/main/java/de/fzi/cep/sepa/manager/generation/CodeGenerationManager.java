package de.fzi.cep.sepa.manager.generation;

import java.io.File;

import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;

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
		return new FlinkSepaCodeGenerator(config, element).createProject();
		//return null;
		
	}
}
