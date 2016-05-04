package de.fzi.cep.sepa.streampipes.codegeneration;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.ConfigGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.ControllerGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.InitGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.ParametersGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.ProgramGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.XmlGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

public class FlinkSepaCodeGenerator extends CodeGenerator {
	private String packageName;
	private String name;
	private String version;
	private String port;

	private String target;
	private String src;
	private String resources;
	private String test;
	private String webInf;

	public FlinkSepaCodeGenerator(DeploymentConfiguration config, SepaDescription element) {
		super(config, element);
		packageName = config.getGroupId() + "." + config.getArtifactId();
		name = config.getClassNamePrefix();
		version = "0.0.1-SNAPSHOT";
		port = Integer.toString(config.getPort());

		target = getTempDir() + "target" + File.separator;
		src = getTempDir() + "src" + File.separator + "main" + File.separator + "java" + File.separator;
		resources = getTempDir() + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
		test = getTempDir() + "src" + File.separator + "test" + File.separator;
		webInf = getTempDir() + "WebContent" + File.separator + "WEB-INF" + File.separator;

	}

	@Override
	protected void create() {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new ParametersGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(element, name, packageName, port).build(), src);
		Utils.writeToFile(new ProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}

	@Override
	protected void createDirectoryStructure() {
		boolean success = (new File(target)).mkdirs();
		success = (new File(src)).mkdirs();
		success = (new File(resources)).mkdirs();
		success = (new File(test)).mkdirs();
		success = (new File(webInf)).mkdirs();

		if (!success) {
			try {
				throw new Exception("Couldn't create folder structure");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
