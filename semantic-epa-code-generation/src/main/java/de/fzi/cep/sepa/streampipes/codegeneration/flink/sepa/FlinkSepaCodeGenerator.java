package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

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
import de.fzi.cep.sepa.streampipes.codegeneration.flink.FlinkCodeGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.FlinkControllerGenerator;
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
		Utils.writeToFile(new FlinkControllerGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(element, name, packageName, port).build(), src);
		Utils.writeToFile(new ProgramGenerator(element, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(element, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName, version);
		Utils.writeToFile(xmlGenerator.getPomFile(), getTempDir() + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");

	}

}
