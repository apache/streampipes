package de.fzi.cep.sepa.storm.sentiment.controller;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class SentimentDetectionParameters extends BindingParameters {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SentimentDetectionParameters(SepaInvocation graph) {
		super(graph);
	}

}