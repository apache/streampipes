package org.streampipes.codegeneration.flink.sec;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.codegeneration.flink.ConfigGenerator;
import org.streampipes.codegeneration.flink.FlinkCodeGenerator;
import org.streampipes.codegeneration.flink.XmlGenerator;
import org.streampipes.codegeneration.utils.Utils;

public class FlinkSecCodeGenerator extends FlinkCodeGenerator {

	public FlinkSecCodeGenerator(DeploymentConfiguration config, SecDescription element) {
		super(config, element);
	}

	@Override
	protected void create() {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new FlinkSecControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new FlinkSecProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(true), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}

}
