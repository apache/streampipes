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

package org.streampipes.codegeneration.flink.sec;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.codegeneration.ControllerGenerator;
import org.streampipes.codegeneration.utils.JFC;

public class FlinkSecControllerGenerator extends ControllerGenerator {
	private ClassName program;
	private ClassName config;

	public FlinkSecControllerGenerator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super(element, name, packageName);
		program = ClassName.get(packageName, name + "Program");
		config = ClassName.get(packageName, "Config");
	}

	public Builder isVisualizable() {
		Builder b = MethodSpec.methodBuilder("isVisualizable").addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC).addStatement("return false").returns(boolean.class);

		return b;
	}

	public Builder getHtml() {
		Builder b = MethodSpec.methodBuilder("getHtml").addAnnotation(Override.class).addModifiers(Modifier.PUBLIC)
				.addParameter(JFC.SEC_INVOCATION, "graph").addStatement("return null").returns(JFC.STRING);

		return b;
	}

	public Builder getRuntime() {
		Builder b = MethodSpec.methodBuilder("getRuntime").addAnnotation(Override.class)
				.addModifiers(Modifier.PROTECTED).addParameter(DataSinkInvocation.class, "graph")
				.addStatement("return new $T(graph, new $T($T.JAR_FILE, $T.FLINK_HOST, $T.FLINK_PORT))", program,
						JFC.FLINK_DEPLOYMENT_CONFIG, config, config, config)
				.returns(JFC.FLINK_SEC_RUNTIME);

		return b;
	}

	@Override
	public JavaFile build() {
		MethodSpec declareModel = getDeclareModelCode(JFC.SEC_DESCRIPTION).build();
		MethodSpec getRuntime = getRuntime().build();
		MethodSpec isVisualizable = isVisualizable().build();
		MethodSpec getHtml = getHtml().build();

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Controller").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.ABSTRACT_FLINK_CONSUMER_DECLARER).addMethod(declareModel).addMethod(isVisualizable)
				.addMethod(getHtml).addMethod(getRuntime).build();

		return JavaFile.builder(packageName, controllerClass).build();
	}

}
