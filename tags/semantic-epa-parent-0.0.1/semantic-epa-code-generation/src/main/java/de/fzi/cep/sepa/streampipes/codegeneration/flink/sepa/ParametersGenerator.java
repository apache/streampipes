package de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.builder.StaticProperties;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.streampipes.codegeneration.Generator;
import de.fzi.cep.sepa.streampipes.codegeneration.utils.JFC;

public class ParametersGenerator extends Generator {

	public ParametersGenerator(ConsumableSEPAElement element, String name, String packageName) {
		super(element, name, packageName);
	}

	public MethodSpec getConstructor() {
		Builder b = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEPA_INVOCATION, "graph").addStatement("super(graph)");

		for (StaticProperty sp : element.getStaticProperties()) {
			b.addParameter(JFC.STRING, sp.getInternalName());
			b.addStatement("this.$N = $N", sp.getInternalName(), sp.getInternalName());
		}

		return b.build();

	}
	

	@Override
	public JavaFile build() {
		MethodSpec constructor = getConstructor();

		TypeSpec.Builder parameterClass = TypeSpec.classBuilder(name + "Parameters").addModifiers(Modifier.PUBLIC)
				.superclass(BindingParameters.class).addMethod(constructor);

		for (StaticProperty sp : element.getStaticProperties()) {
			String internalName = sp.getInternalName();
			parameterClass.addField(JFC.STRING, internalName, Modifier.PRIVATE);
			MethodSpec getter = MethodSpec.methodBuilder(getterName(internalName)).addModifiers(Modifier.PUBLIC)
				.returns(JFC.STRING).addStatement("return " + internalName).build();
			parameterClass.addMethod(getter);
		}


		return JavaFile.builder(packageName, parameterClass.build()).build();
	}

	private String getterName(String s) {
		String result = s;
		if (s != null && s.length() > 0) {
			char first = Character.toUpperCase(s.charAt(0));
			result = "get" + first + s.substring(1);
		}
		
		return result;
	}

}
