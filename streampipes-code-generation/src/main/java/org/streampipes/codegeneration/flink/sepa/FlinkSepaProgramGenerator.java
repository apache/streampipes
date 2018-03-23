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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;

public class FlinkSepaProgramGenerator extends Generator {

	public FlinkSepaProgramGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
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
