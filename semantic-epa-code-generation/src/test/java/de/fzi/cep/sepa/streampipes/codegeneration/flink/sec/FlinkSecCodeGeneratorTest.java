package de.fzi.cep.sepa.streampipes.codegeneration.flink.sec;

import org.junit.Test;

import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.CodeGenerator_RENAME;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.TV;

public class FlinkSecCodeGeneratorTest {

	@Test
	public void testCreateProject() {
		DeploymentConfiguration con = new DeploymentConfiguration("com.example.com", "time", "TimeStamp", 8080);
		SecDescription sec = TV.getSecDescription();
		
		CodeGenerator_RENAME cg = new FlinkSecCodeGenerator(con, sec);

		cg.createProject();
	}

}
