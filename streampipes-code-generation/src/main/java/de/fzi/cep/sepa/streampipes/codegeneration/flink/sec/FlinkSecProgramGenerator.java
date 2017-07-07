package de.fzi.cep.sepa.streampipes.codegeneration.flink.sec;

import java.io.Serializable;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class FlinkSecProgramGenerator extends Generator {

	public FlinkSecProgramGenerator(ConsumableSEPAElement element, String name, String packageName) {
		super(element, name, packageName);
	}

	@Override
	public JavaFile build() {
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(JFC.MAP, JFC.STRING, JFC.OBJECT);
		ParameterizedTypeName dataStreamSink = ParameterizedTypeName.get(JFC.DATA_STREAM_SINK, mapStringObject);
		ParameterizedTypeName dataStream = ParameterizedTypeName.get(JFC.DATA_STREAM, mapStringObject);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addStatement("super(graph)").build();

		MethodSpec constructorConfig = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addParameter(JFC.FLINK_DEPLOYMENT_CONFIG, "config")
				.addStatement("super(graph, config)").build();

		MethodSpec getApplicationLogic = MethodSpec.methodBuilder("getSink").addAnnotation(JFC.OVERRIDE)
				.addModifiers(Modifier.PUBLIC).returns(dataStreamSink).addParameter(dataStream, "convertedStream")
				.addCode("// TODO implement\nreturn null;\n").build();

		TypeSpec programClass = TypeSpec.classBuilder(name + "Program").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.FLINK_SEC_RUNTIME).addSuperinterface(Serializable.class).addMethod(constructor)
				.addMethod(constructorConfig).addMethod(getApplicationLogic).build();

		return JavaFile.builder(packageName, programClass).build();
	}

}
