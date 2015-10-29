package de.fzi.cep.sepa.manager.generation;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;

public class StormArchetypeGenerator extends AbstractArchetypeGenerator {

	public StormArchetypeGenerator(DeploymentConfiguration config, NamedSEPAElement element) {
		super(config, element);
	}
	
	protected String getMavenCommand() {
		return "archetype:generate"
				+" -DgroupId=" +config.getGroupId() 
				+" -DartifactId=" +config.getArtifactId()
				+" -DarchetypeVersion=0.0.1-SNAPSHOT"
				+" -DarchetypeGroupId=de.fzi.cep.sepa"
				+" -DarchetypeArtifactId=streampipes-archetype-storm"
				+" -DarchetypeCatalog=local"
				+" -DclassNamePrefix=" +config.getClassNamePrefix()
				+" -DelementName=" +makeName(element.getName());			
	}
	
	
}
