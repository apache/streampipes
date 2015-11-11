package de.fzi.cep.sepa.actions.samples.evaluation;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class EvaluationParameters extends ActionParameters {

	private String path;
	private String timestampProperty;
	
	public EvaluationParameters(String topic, String url, String path, String timestampProperty) {
		super(topic, url);
		this.path = path;
		this.timestampProperty = timestampProperty;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTimestampProperty() {
		return timestampProperty;
	}
	

}
