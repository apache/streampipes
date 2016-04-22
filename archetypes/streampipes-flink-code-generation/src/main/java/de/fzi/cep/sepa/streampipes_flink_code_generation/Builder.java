package de.fzi.cep.sepa.streampipes_flink_code_generation;

import com.squareup.javapoet.JavaFile;

public abstract class Builder {
	protected String name;
	protected String packageName;
	
	public Builder(String name, String packageName) {
		super();
		this.name = name;
		this.packageName = packageName;
	}

	public abstract JavaFile build();

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
