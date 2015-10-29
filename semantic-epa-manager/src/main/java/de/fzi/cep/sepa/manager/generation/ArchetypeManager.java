package de.fzi.cep.sepa.manager.generation;

import java.io.File;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.client.deployment.DeploymentMethod;
import de.fzi.cep.sepa.model.client.deployment.DeploymentType;

public class ArchetypeManager {

	private DeploymentConfiguration config;
	private NamedSEPAElement element;
	
	public ArchetypeManager(DeploymentConfiguration config, NamedSEPAElement element)
	{
		this.config = config;
		this.element = element;
	}
	
	public File getGeneratedFile()
	{
		return getGenerator().getGeneratedArchetype();
	}
	
	private AbstractArchetypeGenerator getGenerator()
	{
		if (config.getDeploymentType() == DeploymentType.SEPA_ESPER)
		{
			if (config.getDeploymentMethod() == DeploymentMethod.STANDALONE) return new EsperStandaloneArchetypeGenerator(config, element);
		}
		else 
		{
			if (config.getDeploymentType() == DeploymentType.SEPA_STORM) return new StormArchetypeGenerator(config, element);
		}
		return null;
	}
}
