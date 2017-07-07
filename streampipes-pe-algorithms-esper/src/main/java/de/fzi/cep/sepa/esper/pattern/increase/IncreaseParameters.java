package de.fzi.cep.sepa.esper.pattern.increase;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class IncreaseParameters extends BindingParameters {

	private Operation operation;
	private int increase;
	private int duration;
	
	private String mapping;


	public IncreaseParameters(SepaInvocation invocationGraph,
			Operation operation, int increase, int duration,
			String mapping) {
		super(invocationGraph);
		this.operation = operation;
		this.increase = increase;
		this.duration = duration;
		this.mapping = mapping;
	}


	public Operation getOperation() {
		return operation;
	}


	public int getIncrease() {
		return increase;
	}


	public int getDuration() {
		return duration;
	}


	public String getMapping() {
		return mapping;
	}

}
