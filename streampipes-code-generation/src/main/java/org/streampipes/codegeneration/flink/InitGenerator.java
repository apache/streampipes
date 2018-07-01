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

package org.streampipes.codegeneration.flink;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.*;
import com.squareup.javapoet.MethodSpec.Builder;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.codegeneration.Generator;
import org.streampipes.codegeneration.utils.JFC;

public class InitGenerator extends Generator {

	private boolean standalone;
	public InitGenerator(ConsumableStreamPipesEntity sepa, String name, String packageName, boolean standalone) {
		super(sepa, name, packageName);

		this.standalone = standalone;
	}

	@Override
	public JavaFile build() {
		List<ClassName> controllers = new ArrayList<>();
		controllers.add(ClassName.get("", name + "Controller"));

		TypeSpec controllerClass = TypeSpec.classBuilder(name + "Init").addModifiers(Modifier.PUBLIC)
				.superclass(JFC.CONTAINER_MODEL_SUBMITTER).addMethod(getInit(controllers))
				.build();
		return JavaFile.builder(packageName, controllerClass).build();
	}

	private MethodSpec getInit(List<ClassName> controllers) {
		Builder b = MethodSpec.methodBuilder("init").addAnnotation(Override.class).returns(TypeName.VOID)
				.addModifiers(Modifier.PUBLIC);
		b.addStatement("$T.getInstance().setRoute($S)", JFC.DECLARERS_SINGLETON, name.toLowerCase());
		for (ClassName cn : controllers) {
			b.addStatement("$T.getInstance().add(new $S())", JFC.DECLARERS_SINGLETON, cn);
		}

		return b.build();
	}

}
