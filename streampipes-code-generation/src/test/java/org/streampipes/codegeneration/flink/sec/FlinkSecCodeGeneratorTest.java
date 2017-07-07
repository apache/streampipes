package org.streampipes.codegeneration.flink.sec;

import org.junit.Test;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.codegeneration.api.ImplementationCodeGenerator;
import org.streampipes.codegeneration.utils.TV;

public class FlinkSecCodeGeneratorTest {

	@Test
	public void testCreateProject() {
		DeploymentConfiguration con = new DeploymentConfiguration("com.example.com", "time", "TimeStamp", 8080);
		SecDescription sec = TV.getSecDescription();
		
		ImplementationCodeGenerator cg = new FlinkSecCodeGenerator(con, sec);

		cg.createProject();
	}

}
