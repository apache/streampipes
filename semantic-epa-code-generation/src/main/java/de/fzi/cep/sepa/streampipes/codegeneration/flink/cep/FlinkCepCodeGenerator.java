package de.fzi.cep.sepa.streampipes.codegeneration.flink.cep;

import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.CodeGenerator;

public class FlinkCepCodeGenerator extends CodeGenerator {

	public FlinkCepCodeGenerator(DeploymentConfiguration config, SepaDescription element) {
		super(config, element);
	}

	@Override
	protected void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createDirectoryStructure() {
	}

}
