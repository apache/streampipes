package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class InitGenerator extends Generator {
	private String port;

	public InitGenerator(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
		port = "8080";
	}

	public MethodSpec getEpaDeclarers(List<ClassName> controllers) {
		return getDeclarers(controllers, "epaDeclarers", JFC.SEMANTIC_EVENT_PROCESSING_AGENT_DECLARER);
	}

	public MethodSpec getSourceDeclarers(List<ClassName> controllers) {
		return getDeclarers(controllers, "sourceDeclarers", JFC.SEMANTIC_EVENT_PRODUCER_DECLARER);
	}

	public MethodSpec getConsumerEpaDeclarers(List<ClassName> controllers) {
		return getDeclarers(controllers, "consumerDeclarers", JFC.SEMANTIC_EVENT_CONSUMER_DECLARER);

	}

	private MethodSpec getPort() {
		Builder b = MethodSpec.methodBuilder("port").addAnnotation(Override.class).returns(int.class)
				.addModifiers(Modifier.PROTECTED);
		b.addStatement("return $L", port);
		return b.build();
	}

	private MethodSpec contextPath() {
		Builder b = MethodSpec.methodBuilder("contextPath").addAnnotation(Override.class).returns(JFC.STRING)
				.addModifiers(Modifier.PROTECTED);
		b.addStatement("return $S", "/" + name.toLowerCase());
		return b.build();
	}

	@Override
	public JavaFile build() {
		List<ClassName> controllers = new ArrayList<ClassName>();
		controllers.add(ClassName.get("", name + "Controller"));

		TypeSpec controllerClass = TypeSpec.classBuilder("Init").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.EMBEDDED_MODEL_SUBMITTER).addMethod(getEpaDeclarers(controllers))
				.addMethod(getSourceDeclarers(null)).addMethod(getConsumerEpaDeclarers(null)).addMethod(getPort())
				.addMethod(contextPath()).build();
		return JavaFile.builder(packageName, controllerClass).build();
	}

	private MethodSpec getDeclarers(List<ClassName> controllers, String methodName, ClassName className) {
		Builder b = MethodSpec.methodBuilder(methodName).addAnnotation(Override.class)
				.returns(ParameterizedTypeName.get(JFC.LIST, className)).addModifiers(Modifier.PROTECTED);
		b.addStatement("$T<$T> result = new $T<$T>()", JFC.LIST, className, JFC.ARRAY_LIST, className);
		if (controllers != null) {
			for (ClassName c : controllers) {
				b.addStatement("result.add(new $T())", c);
			}
		}

		b.addStatement("return result");

		return b.build();
	}

}
