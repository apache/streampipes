package org.streampipes.codegeneration;

import com.squareup.javapoet.JavaFile;

import org.streampipes.model.ConsumableSEPAElement;

public abstract class Generator {
	protected String name;
	protected String packageName;
	protected ConsumableSEPAElement element;
	
	public Generator(ConsumableSEPAElement element, String name, String packageName) {
		super();
		this.element = element;
		this.name = name;
		this.packageName = packageName;
	}

	public abstract JavaFile build();
	
	public ConsumableSEPAElement getElement() {
		return element;
	}

	public void setElement(ConsumableSEPAElement element) {
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
