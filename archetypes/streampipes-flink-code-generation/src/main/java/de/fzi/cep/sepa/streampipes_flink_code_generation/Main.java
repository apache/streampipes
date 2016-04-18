package de.fzi.cep.sepa.streampipes_flink_code_generation;

import java.io.File;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 *
 */
public class Main {
	private static String path = "/home/philipp/FZI/";
	private static String root = path + "a_flinkTemplatetest/";
	private static String target = root + "target/";
	private static String src = root + "src/main/java/";
	private static String resources = root + "src/main/resources/";
	private static String test = root + "src/test/";

	public static void main(String[] args) throws Exception {
		String name = "TestProject";
		String packageName = "de.fzi.cep.sepa.flink.test.project";
		createProject(name, packageName);
	}

	public static void createProject(String name, String packageName) throws Exception {
		createDirectoryStructure();
		Utils.writeToFile(createImplementation(name, packageName), src);
		Utils.writeToFile(createParameters(name, packageName), src);
		Utils.writeToFile(createController(name, packageName), src);
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
		ClassName flinkDeploymentConfig = ClassName.get("de.fzi.cep.sepa.flink", "FlinkDeploymentConfig");
		ClassName flinkSepaRuntime = ClassName.get("de.fzi.cep.sepa.flink", "FlinkSepaRuntime");
		ClassName override = ClassName.get("", "Override");
		ClassName map = ClassName.get("java.util", "Map");
		ClassName string = ClassName.get("", "String");
		ClassName object = ClassName.get("", "Object");
		ClassName dataStream = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStream");
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(map, string, object);
		ParameterizedTypeName d = ParameterizedTypeName.get(dataStream, mapStringObject);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(parameters, "params").addStatement("super(params)").build();

		MethodSpec constructorConfig = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(parameters, "params").addParameter(flinkDeploymentConfig, "config")
				.addStatement("super(params, config)").build();

		MethodSpec getApplicationLogic = MethodSpec.methodBuilder("getApplicationLogic").addAnnotation(override)
				.addModifiers(Modifier.PROTECTED).returns(d)
				.addParameter(d, "messageStream").addCode("return (DataStream<Map<String, Object>>) messageStream\n"
						+ "  .flatMap(new " + name + "(params.getPropertyName()));")
				.build();

		TypeSpec programClass = TypeSpec.classBuilder(name + "Program").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(flinkSepaRuntime, parameters)).addMethod(constructor)
				.addMethod(constructorConfig).addMethod(getApplicationLogic).build();

		return JavaFile.builder(packageName, programClass).build();
	}

	public static JavaFile createImplementation(String name, String packageName) {
		ClassName map = ClassName.get("java.util", "Map");
		ClassName string = ClassName.get("", "String");
		ClassName object = ClassName.get("", "Object");
		ClassName exception = ClassName.get("", "Exception");
		ClassName override = ClassName.get("", "Override");
		ClassName flatMapFunction = ClassName.get("org.apache.flink.api.common.functions", "FlatMapFunction");
		ClassName collector = ClassName.get("org.apache.flink.util", "Collector");
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(map, string, object);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(string, "propertyName").addStatement("this.$N = $N", "propertyName", "propertyName")
				.build();

		TypeName in = mapStringObject;
		TypeName out = ParameterizedTypeName.get(collector, mapStringObject);

		MethodSpec flatMap = MethodSpec.methodBuilder("flatMap").addAnnotation(override).addModifiers(Modifier.PUBLIC)
				.addParameter(in, "in").addParameter(out, "out").addException(exception)
				.addStatement("Object o = in.get(propertyName)").addCode("//TODO implement\n").build();

		TypeSpec parameterClass = TypeSpec.classBuilder("TestProject").addModifiers(Modifier.PUBLIC)
				.addSuperinterface(ParameterizedTypeName.get(flatMapFunction, mapStringObject, mapStringObject))
				.addField(string, "propertyName", Modifier.PRIVATE).addMethod(constructor).addMethod(flatMap).build();

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

	public static JavaFile createController(String name, String packageName) {
		ClassName declareUtils = ClassName.get("de.fzi.cep.sepa.util", "DeclarerUtils");
		ClassName resources = ClassName.get("com.google.common.io", "Resources");
		ClassName sepaParseException = ClassName.get("de.fzi.cep.sepa.commons.exceptions", "SepaParseException");
		MethodSpec declareModel = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).returns(SepaDescription.class)
				.addCode("" + "try { \n" + "	return $T.descriptionFromResources($T.getResource(\""
						+ name.toLowerCase() + ".jsonld\"),\n" + "		SepaDescription.class);\n"
						+ "} catch ($T e) {\n" + "	e.printStackTrace();\n" + "	return null;\n"

						+ "}\n", declareUtils, resources, sepaParseException)
				.build();

		ClassName flinkSepaRuntime = ClassName.get("de.fzi.cep.sepa.flink", "FlinkSepaRuntime");
		ClassName parameters = ClassName.get(packageName, name + "Parameters");

		MethodSpec getRuntime = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SepaInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(flinkSepaRuntime, parameters)).addCode("//TODO\nreturn null;\n").build();

		ClassName abstractFlinkAgentDeclarer = ClassName.get("de.fzi.cep.sepa.flink", "AbstractFlinkAgentDeclarer");

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(abstractFlinkAgentDeclarer, parameters)).addMethod(declareModel)
				.addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();

		// javaFile.writeTo(System.out);

	}

	public static String createPomFile(String name, String packageName) {
		String pom = Utils.readResourceFile("pom");
		pom = pom.replace("####name####", name.toLowerCase());
		return pom;
	}
}
