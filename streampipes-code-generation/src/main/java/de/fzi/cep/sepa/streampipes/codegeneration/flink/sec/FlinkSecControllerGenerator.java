package de.fzi.cep.sepa.streampipes.codegeneration.flink.sec;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.streampipes.codegeneration.ControllerGenerator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class FlinkSecControllerGenerator extends ControllerGenerator {
	private ClassName program;
	private ClassName config;

	public FlinkSecControllerGenerator(ConsumableSEPAElement element, String name, String packageName) {
		super(element, name, packageName);
		program = ClassName.get(packageName, name + "Program");
		config = ClassName.get(packageName, "Config");
	}

	public Builder isVisualizable() {
		Builder b = MethodSpec.methodBuilder("isVisualizable").addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC).addStatement("return false").returns(boolean.class);

		return b;
	}

	public Builder getHtml() {
		Builder b = MethodSpec.methodBuilder("getHtml").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addStatement("return null").returns(JFC.STRING);

		return b;
	}

	public Builder getRuntime() {
		Builder b = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(SecInvocation.class, "graph")
				.addStatement("return new $T(graph, new $T($T.JAR_FILE, $T.FLINK_HOST, $T.FLINK_PORT))", program,
						JFC.FLINK_DEPLOYMENT_CONFIG, config, config, config)
				.returns(JFC.FLINK_SEC_RUNTIME);

		return b;
	}

	@Override
	public JavaFile build() {
		MethodSpec declareModel = getDeclareModelCode(JFC.SEC_DESCRIPTION).build();
		MethodSpec getRuntime = getRuntime().build();
		MethodSpec isVisualizable = isVisualizable().build();
		MethodSpec getHtml = getHtml().build();

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.ABSTRACT_FLINK_CONSUMER_DECLARER).addMethod(declareModel).addMethod(isVisualizable)
				.addMethod(getHtml).addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

}
