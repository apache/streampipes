package de.fzi.cep.sepa.storm.utils;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.param.BindingParameters;

public class Parameters extends BindingParameters {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Parameters(SepaInvocation graph) {
		super(graph);
	}

}