package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.ConfigGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.FlinkCodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.InitGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.XmlGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSepaCodeGenerator extends FlinkCodeGenerator {

	public FlinkSepaCodeGenerator(DeploymentConfiguration config, SepaDescription element) {
		super(config, element);
	}

	@Override
	protected void create() {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new ParametersGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new FlinkSepaControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(element, name, packageName, port).build(), src);
		Utils.writeToFile(new ProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}

}
