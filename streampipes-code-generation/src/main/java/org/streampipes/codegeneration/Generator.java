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
