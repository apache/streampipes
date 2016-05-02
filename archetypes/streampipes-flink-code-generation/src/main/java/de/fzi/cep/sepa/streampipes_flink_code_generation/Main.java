package de.fzi.cep.sepa.streampipes_flink_code_generation;

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

/**
 *
 */
public class Main {
	private static String path = "/home/philipp/FZI/";
	private static String root = path + "aa_flinkTemplatetest/";
	private static String target = root + "target/";
	private static String src = root + "src/main/java/";
	private static String resources = root + "src/main/resources/";
	private static String test = root + "src/test/";

	public static void main(String[] args) throws Exception {
		String name = "TestProject";
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
		Utils.writeToFile(createImplementation(name, packageName), src);
		Utils.writeToFile(createParameters(name, packageName), src);
		Utils.writeToFile(new ControllerGenerator(sepa, name, packageName).build(), src);
		Utils.writeToFile(createProgram(name, packageName), src);
		Utils.writeToFile(createPomFile(name, packageName), root + "pom.xml");

	}

	public static void createDirectoryStructure() throws Exception {
		boolean success = (new File(target)).mkdirs();
		success = (new File(src)).mkdirs();
		success = (new File(resources)).mkdirs();
		success = (new File(test)).mkdirs();

		if (!success) {
			throw new Exception("Couldn't create folder structure");
		}
	}

	public static JavaFile createProgram(String name, String packageName) {
		ClassName parameters = ClassName.get("", name + "Parameters");
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(JFC.MAP, JFC.STRING, JFC.OBJECT);
		ParameterizedTypeName d = ParameterizedTypeName.get(JFC.DATA_STREAM, mapStringObject);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(parameters, "params").addStatement("super(params)").build();

		MethodSpec constructorConfig = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(parameters, "params").addParameter(JFC.FLINK_DEPLOYMENT_CONFIG, "config")
				.addStatement("super(params, config)").build();

		MethodSpec getApplicationLogic = MethodSpec.methodBuilder("getApplicationLogic").addAnnotation(JFC.OVERRIDE)
				.addModifiers(Modifier.PROTECTED).returns(d)
				.addParameter(d, "messageStream").addCode("return ($T<Map<String, Object>>) messageStream\n"
						+ "  .flatMap(new " + name + "(params.getPropertyName()));", JFC.DATA_STREAM)
				.build();

		TypeSpec programClass = TypeSpec.classBuilder(name + "Program").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters)).addMethod(constructor)
				.addMethod(constructorConfig).addMethod(getApplicationLogic).build();

		return JavaFile.builder(packageName, programClass).build();
	}

	public static JavaFile createImplementation(String name, String packageName) {
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(JFC.MAP, JFC.STRING, JFC.OBJECT);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.STRING, "propertyName").addStatement("this.$N = $N", "propertyName", "propertyName")
				.build();

		TypeName in = mapStringObject;
		TypeName out = ParameterizedTypeName.get(JFC.COLLECTOR, mapStringObject);

		MethodSpec flatMap = MethodSpec.methodBuilder("flatMap").addAnnotation(JFC.OVERRIDE).addModifiers(Modifier.PUBLIC)
				.addParameter(in, "in").addParameter(out, "out").addException(JFC.EXCEPTION)
				.addStatement("Object o = in.get(propertyName)").addCode("//TODO implement\n").build();

		TypeSpec parameterClass = TypeSpec.classBuilder("TestProject").addModifiers(Modifier.PUBLIC)
				.addSuperinterface(ParameterizedTypeName.get(JFC.FLAT_MAP_FUNCTION, mapStringObject, mapStringObject))
				.addField(JFC.STRING, "propertyName", Modifier.PRIVATE).addMethod(constructor).addMethod(flatMap).build();

		return JavaFile.builder(packageName, parameterClass).build();

	}

	public static JavaFile createParameters(String name, String packageName) {
		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(SepaInvocation.class, "graph").addStatement("super(graph)")
				.addParameter(String.class, "propertyName").addStatement("this.$N = $N", "propertyName", "propertyName")
				.addParameter(String.class, "outputProperty")
				.addStatement("this.$N = $N", "outputProperty", "outputProperty").build();

		MethodSpec getPropertyName = MethodSpec.methodBuilder("getPropertyName").addModifiers(Modifier.PUBLIC)
				.returns(String.class).addStatement("return propertyName").build();

		MethodSpec getOutputProperty = MethodSpec.methodBuilder("getOutputProperty").addModifiers(Modifier.PUBLIC)
				.returns(String.class).addStatement("return outputProperty").build();

		TypeSpec parameterClass = TypeSpec.classBuilder(name + "Parameters").addModifiers(Modifier.PUBLIC)
				.superclass(BindingParameters.class).addField(String.class, "propertyName", Modifier.PRIVATE)
				.addField(String.class, "outputProperty", Modifier.PRIVATE).addMethod(constructor)
				.addMethod(getPropertyName).addMethod(getOutputProperty).build();

		return JavaFile.builder(packageName, parameterClass).build();
	}


	public static String createPomFile(String name, String packageName) {
		String pom = Utils.readResourceFile("pom");
		pom = pom.replace("####name####", name.toLowerCase());
		return pom;
	}
}
