package de.fzi.cep.sepa.streampipes_flink_code_generation;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class ControllerBuilder extends Builder {

	public ControllerBuilder(String name, String packageName) {
		super(name, packageName);
	}
	
	@Override
	public JavaFile build() {
		MethodSpec declareModel = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).returns(SepaDescription.class)
				.addCode("" + "try { \n" + "	return $T.descriptionFromResources($T.getResource(\""
						+ name.toLowerCase() + ".jsonld\"),\n" + "		SepaDescription.class);\n"
						+ "} catch ($T e) {\n" + "	e.printStackTrace();\n" + "	return null;\n"

						+ "}\n", JFC.DECLARE_UTILS, JFC.RESOURCES, JFC.SEPA_PARSE_EXCEPTION)
				.build();

		ClassName parameters = ClassName.get(packageName, name + "Parameters");

		MethodSpec getRuntime = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SepaInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters)).addCode("//TODO\nreturn null;\n").build();

		ClassName abstractFlinkAgentDeclarer = ClassName.get("de.fzi.cep.sepa.flink", "AbstractFlinkAgentDeclarer");

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(abstractFlinkAgentDeclarer, parameters)).addMethod(declareModel)
				.addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();

		// javaFile.writeTo(System.out);

	}

	@Override
	public String toString() {
		return build().toString();
	}
}
