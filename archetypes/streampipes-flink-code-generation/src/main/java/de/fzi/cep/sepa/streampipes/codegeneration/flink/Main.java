package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
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
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

/**
 *
 */
public class Main {
	private static String root = "/home/philipp/FZI/a_delme_flinkTemplatetest/";

	private static String target = root + "target/";
	private static String src = root + "src/main/java/";
	private static String resources = root + "src/main/resources/";
	private static String test = root + "src/test/";
	private static String webInf = root + "WebContent/WEB-INF/";


	public static void main(String[] args) throws Exception {
		String name = "NewTestProject";
		String packageName = "de.fzi.cep.sepa.flink.test.project";
		
		SepaDescription sepa = new SepaDescription("sepa/testproject", name, "Test description");
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = PrimitivePropertyBuilder.createPropertyRestriction("http://test.org#test1").build();
		eventProperties.add(e1);
		
		EventStream stream1 = StreamBuilder
				.createStreamRestriction("localhost/sepa/testproject")
				.schema(
						SchemaBuilder.create()
							.properties(eventProperties)
							.build()
						).build();
		sepa.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
		List<EventProperty> appendProperties = new ArrayList<EventProperty>();
		appendProperties.add(new EventPropertyPrimitive(XSD._long.toString(),
				"appendedTime", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		sepa.setOutputStrategies(strategies);
		

		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "mappingFirst",
				"Mapping First: ", ""));
		staticProperties.add(new FreeTextStaticProperty("freeText", "Free Text: ", ""));
		
		sepa.setStaticProperties(staticProperties);
		
		

		createProject(sepa, name, packageName);
	}

	public static void createProject(SepaDescription sepa, String name, String packageName) throws Exception {
		createDirectoryStructure();

		// source files
//		Utils.writeToFile(new ImplementationGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ParametersGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ControllerGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ProgramGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ConfigGenerator(sepa, name, packageName).build(), src);

		// xml files
		XmlGenerator xmlGenerator = new XmlGenerator(name, packageName);
		Utils.writeToFile(xmlGenerator.getPomFile(), root + "pom.xml");
		Utils.writeToFile(xmlGenerator.getWebXmlFile(), webInf + "web.xml");
	}

	public static void createDirectoryStructure() throws Exception {
		boolean success = (new File(target)).mkdirs();
		success = (new File(src)).mkdirs();
		success = (new File(resources)).mkdirs();
		success = (new File(test)).mkdirs();
		success = (new File(webInf)).mkdirs();

		if (!success) {
			throw new Exception("Couldn't create folder structure");
		}
	}

}
