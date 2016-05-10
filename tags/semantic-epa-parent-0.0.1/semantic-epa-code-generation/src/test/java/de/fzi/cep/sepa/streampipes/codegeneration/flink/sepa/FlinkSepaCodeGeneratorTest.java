package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.CodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;

public class FlinkSepaCodeGeneratorTest {

	@Test
	public void testCreateProject() {
		DeploymentConfiguration con = new DeploymentConfiguration("com.example.com", "time", "TimeStamp", 8080);
		SepaDescription sepa = TV.getSepa();

		CodeGenerator cg = new FlinkSepaCodeGenerator(con, sepa);

		cg.createProject();
	}

}
