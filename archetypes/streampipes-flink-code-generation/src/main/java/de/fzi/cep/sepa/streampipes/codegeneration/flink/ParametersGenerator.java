package de.fzi.cep.sepa.streampipes.codegeneration.flink;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;

public class ParametersGenerator extends Generator {

	public ParametersGenerator(SepaDescription sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
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

}
