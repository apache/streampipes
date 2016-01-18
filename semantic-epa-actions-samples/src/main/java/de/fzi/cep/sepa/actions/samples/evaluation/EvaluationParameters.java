package de.fzi.cep.sepa.actions.samples.evaluation;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class EvaluationParameters extends ActionParameters {

	private String timestampProperty;
	
	public EvaluationParameters(String topic, String url, String timestampProperty) {
		super(topic, url);
		this.timestampProperty = timestampProperty;
	}

	public String getTimestampProperty() {
		return timestampProperty;
	}
	

}
