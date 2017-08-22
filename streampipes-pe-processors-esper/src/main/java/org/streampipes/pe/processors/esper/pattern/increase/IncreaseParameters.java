package org.streampipes.pe.processors.esper.pattern.increase;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

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
