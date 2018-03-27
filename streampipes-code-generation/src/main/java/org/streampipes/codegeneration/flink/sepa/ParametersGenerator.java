/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.codegeneration.flink.sepa;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;
import org.streampipes.codegeneration.utils.Utils;
import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;

import javax.lang.model.element.Modifier;

public class ParametersGenerator extends Generator {

	public ParametersGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
	}

	public MethodSpec getConstructor() {
		Builder b = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEPA_INVOCATION, "graph").addStatement("super(graph)");

		for (StaticProperty sp : element.getStaticProperties()) {
			String internalNameCamelCased = Utils.toCamelCase(sp.getInternalName());
			b.addParameter(JFC.STRING, internalNameCamelCased);
			b.addStatement("this.$N = $N", internalNameCamelCased, internalNameCamelCased);
		}

		return b.build();

	}
	

	@Override
	public JavaFile build() {
		MethodSpec constructor = getConstructor();

		TypeSpec.Builder parameterClass = TypeSpec.classBuilder(name + "Parameters").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.EVENT_PROCESSOR_BINDING_PARAMS).addMethod(constructor);

		for (StaticProperty sp : element.getStaticProperties()) {
			String internalName = Utils.toCamelCase(sp.getInternalName());
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
