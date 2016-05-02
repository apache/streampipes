package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class ProgramGenerator extends Generator {

	public ProgramGenerator(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
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
				.addParameter(d, "messageStream")
				.addCode("// TODO implement\nreturn messageStream;\n")
				.build();

		TypeSpec programClass = TypeSpec.classBuilder(name + "Program").addModifiers(Modifier.PUBLIC)
				.superclass(ParameterizedTypeName.get(JFC.FLINK_SEPA_RUNTIME, parameters)).addMethod(constructor)
				.addMethod(constructorConfig).addMethod(getApplicationLogic).build();

		return JavaFile.builder(packageName, programClass).build();
	}

}
