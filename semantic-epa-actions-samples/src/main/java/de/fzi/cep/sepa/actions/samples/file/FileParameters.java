package de.fzi.cep.sepa.actions.samples.file;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class FileParameters extends ActionParameters {

	private String path;
	
	public FileParameters(String topic, String url, String path) {
		super(topic, url);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	

}
