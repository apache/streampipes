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

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;

public class ImplementationGenerator extends Generator {

	public ImplementationGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
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
