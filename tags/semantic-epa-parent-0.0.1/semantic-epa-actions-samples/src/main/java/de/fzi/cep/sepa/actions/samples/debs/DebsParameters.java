package de.fzi.cep.sepa.actions.samples.debs;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class DebsParameters extends ActionParameters {

	private String path;
	
	public DebsParameters(String topic, String url, String path) {
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
