package org.streampipes.codegeneration.flink.sepa;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.codegeneration.ControllerGenerator;
import org.streampipes.codegeneration.utils.JFC;

public class FlinkSepaControllerGenerator extends ControllerGenerator {

	private ClassName parameters;
	private ClassName program;
	private ClassName config;

	public FlinkSepaControllerGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
		parameters = ClassName.get(packageName, name + "Parameters");
		program = ClassName.get(packageName, name + "Program");
		config = ClassName.get(packageName, "Config");
	}

	public Builder getRuntime() {
		Builder b = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(DataProcessorInvocation.class, "graph")
				.returns(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters));

		
		for (StaticProperty sp : element.getStaticProperties()) {
			getStaticProperty(b, sp);
		}

		String staticParam = "$T staticParam = new $T(graph, ";
		for (StaticProperty sp : element.getStaticProperties()) {
			staticParam = staticParam + sp.getInternalName() + ", ";
		}
		staticParam = staticParam.subSequence(0, staticParam.length() - 2) + ")";
		b.addStatement(staticParam, parameters, parameters);

		b.addStatement("return new $T(staticParam, new $T($T.JAR_FILE, $T.FLINK_HOST, $T.FLINK_PORT))", program,
				JFC.FLINK_DEPLOYMENT_CONFIG, config, config, config);

		return b;
	}

	private Builder getStaticProperty(Builder b, StaticProperty sp) {
		String name = sp.getInternalName().replaceAll("-", "_").replaceAll("/", "_");
		if (sp instanceof MappingProperty) {
			b.addStatement("String $L = $T.getMappingPropertyName(graph, $S)", name, JFC.SEPA_UTILS,
					sp.getInternalName());
		} else if (sp instanceof FreeTextStaticProperty) {
			b.addStatement("String $L = $T.getFreeTextStaticPropertyValue(graph, $S)", name, JFC.SEPA_UTILS,
					sp.getInternalName());
		} else {
			// TODO add implementation for the other strategies
			try {
				throw new Exception("Not yet Implemented");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	@Override
	public JavaFile build() {
		MethodSpec declareModel = getDeclareModelCode(JFC.SEPA_DESCRIPTION).build();
		MethodSpec getRuntime = getRuntime().build();

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(JFC.ABSTRACT_FLINK_AGENT_DECLARER, parameters))
				.addMethod(declareModel).addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

	@Override
	public String toString() {
		return build().toString();
	}
}
