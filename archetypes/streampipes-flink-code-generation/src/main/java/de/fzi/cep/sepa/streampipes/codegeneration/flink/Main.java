package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.Utils;

/**
 *
 */
public class Main {
	private static String path = "/home/philipp/FZI/";
	private static String root = path + "a_delme_flinkTemplatetest/";
	private static String target = root + "target/";
	private static String src = root + "src/main/java/";
	private static String resources = root + "src/main/resources/";
	private static String test = root + "src/test/";
	private static String webInf = root + "WebContent/WEB-INF/";


	public static void main(String[] args) throws Exception {
		String name = "NewTestProject";
		String packageName = "de.fzi.cep.sepa.flink.test.project";
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription sepa = new SepaDescription("sepa/testproject", name, "Test description");
		sepa.addEventStream(stream1);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();
		List<EventProperty> appendProperties = new ArrayList<EventProperty>();
		appendProperties.add(new EventPropertyPrimitive(XSD._long.toString(),
				"appendedTime", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		sepa.setOutputStrategies(strategies);

		createProject(sepa, name, packageName);
	}

	public static void createProject(SepaDescription sepa, String name, String packageName) throws Exception {
		createDirectoryStructure();

		// source files
		Utils.writeToFile(new ImplementationGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ParametersGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ControllerGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new InitGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(new ProgramGenerator(sepa, name, packageName).build(), src);

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
