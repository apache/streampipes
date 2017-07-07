package org.streampipes.manager.generation.code;

public class FieldName {

	private String name;
	private Class<?> clazz;
	private Class<?> narrowClazz;
	
	public FieldName(String name, Class<?> clazz) {
		super();
		this.name = name;
		this.clazz = clazz;
	}
	
	public FieldName(String name, Class<?> clazz, Class<?> narrowClazz) {
		this(name, clazz);
		this.narrowClazz = narrowClazz;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getNarrowClazz() {
		return narrowClazz;
	}

	public void setNarrowClazz(Class<?> narrowClazz) {
		this.narrowClazz = narrowClazz;
	}
	
	public boolean isClassNarrowed() {
		return narrowClazz != null;
	}
	
	
	
}
