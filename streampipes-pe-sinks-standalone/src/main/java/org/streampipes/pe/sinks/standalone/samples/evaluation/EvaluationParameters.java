package org.streampipes.pe.sinks.standalone.samples.evaluation;

import org.streampipes.pe.sinks.standalone.samples.ActionParameters;

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
