package org.streampipes.pe.algorithms.esper.compose;

public class SelectProperty {

	private String originalName;
	private String asName;
	
	public SelectProperty(String originalName, String asName) {
		this.originalName = originalName;
		this.asName = asName;
	}

	public SelectProperty() {
		// TODO Auto-generated constructor stub
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getAsName() {
		return asName;
	}

	public void setAsName(String asName) {
		this.asName = asName;
	}
	
	

}
