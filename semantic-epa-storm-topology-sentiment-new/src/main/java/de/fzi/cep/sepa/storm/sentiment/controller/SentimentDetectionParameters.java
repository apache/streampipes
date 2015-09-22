package de.fzi.cep.sepa.storm.sentiment.controller;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class SentimentDetectionParameters extends BindingParameters {
	
	private String sentimentMapsTo;
	
	public SentimentDetectionParameters(SepaInvocation graph, String sentimentMapsTo) {
		super(graph);
		this.sentimentMapsTo = sentimentMapsTo;
	}

	public String getSentimentMapsTo() {
		return sentimentMapsTo;
	}

}
