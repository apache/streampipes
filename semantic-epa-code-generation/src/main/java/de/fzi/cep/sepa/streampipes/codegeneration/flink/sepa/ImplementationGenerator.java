package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class ImplementationGenerator extends Generator {

	public ImplementationGenerator(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
		ParameterizedTypeName mapStringObject = ParameterizedTypeName.get(JFC.MAP, JFC.STRING, JFC.OBJECT);

		MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.STRING, "propertyName").addStatement("this.$N = $N", "propertyName", "propertyName")
				.build();

		TypeName in = mapStringObject;
		TypeName out = ParameterizedTypeName.get(JFC.COLLECTOR, mapStringObject);

		MethodSpec flatMap = MethodSpec.methodBuilder("flatMap").addAnnotation(JFC.OVERRIDE).addModifiers(Modifier.PUBLIC)
				.addParameter(in, "in").addParameter(out, "out").addException(JFC.EXCEPTION)
				.addStatement("Object o = in.get(propertyName)").addCode("//TODO implement\n").build();

		TypeSpec parameterClass = TypeSpec.classBuilder(name).addModifiers(Modifier.PUBLIC)
				.addSuperinterface(ParameterizedTypeName.get(JFC.FLAT_MAP_FUNCTION, mapStringObject, mapStringObject))
				.addField(JFC.STRING, "propertyName", Modifier.PRIVATE).addMethod(constructor).addMethod(flatMap).build();

		return JavaFile.builder(packageName, parameterClass).build();
	}

}
