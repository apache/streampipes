package org.streampipes.codegeneration.flink.sepa;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.codegeneration.flink.ConfigGenerator;
import org.streampipes.codegeneration.flink.FlinkCodeGenerator;
import org.streampipes.codegeneration.flink.InitGenerator;
import org.streampipes.codegeneration.flink.XmlGenerator;
import org.streampipes.codegeneration.utils.Utils;

public class FlinkSepaCodeGenerator extends FlinkCodeGenerator {

	private boolean standalone;


	public FlinkSepaCodeGenerator(DeploymentConfiguration config, DataProcessorDescription element, boolean standalone) {
		super(config, element);
		this.standalone = standalone;
	}

	@Override
	protected void create() {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new ParametersGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new FlinkSepaControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(element, name, packageName, standalone).build(), src);
		Utils.writeToFile(new FlinkSepaProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(standalone), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}



}
