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

	public ControllerBuilder(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	private String getDeclareModelCode() {
		return "SepaDescription desc = new SepaDescription(\"" + sepa.getPathName() + "\", \"" + sepa.getName()
				+ "\", \"" + sepa.getDescription() + "\");\n" + "return desc;\n";
	}

	@Override
	public JavaFile build() {
		MethodSpec declareModel = MethodSpec.methodBuilder("declareModel").addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class).returns(SepaDescription.class).addCode(getDeclareModelCode()).build();

		ClassName parameters = ClassName.get(packageName, name + "Parameters");

		MethodSpec getRuntime = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SepaInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters))
				.addCode("//TODO\nreturn null;\n").build();

		ClassName abstractFlinkAgentDeclarer = ClassName.get("de.fzi.cep.sepa.flink", "AbstractFlinkAgentDeclarer");

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(abstractFlinkAgentDeclarer, parameters)).addMethod(declareModel)
				.addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

	@Override
	public String toString() {
		return build().toString();
	}
}
