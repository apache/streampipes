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

package org.streampipes.codegeneration;

import com.squareup.javapoet.JavaFile;

import org.streampipes.model.base.ConsumableStreamPipesEntity;

public abstract class Generator {
	protected String name;
	protected String packageName;
	protected ConsumableStreamPipesEntity element;
	
	public Generator(ConsumableStreamPipesEntity element, String name, String packageName) {
		super();
		this.element = element;
		this.name = name;
		this.packageName = packageName;
	}

	public abstract JavaFile build();
	
	public ConsumableStreamPipesEntity getElement() {
		return element;
	}

	public void setElement(ConsumableStreamPipesEntity element) {
		this.element = element;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	
	
}
