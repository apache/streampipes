package de.fzi.cep.sepa.esper.aggregate.rate;

import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class EventRateParameter extends BindingParameters {

	int avgRate;
	int outputRate;
	String outputName; 
	
	public EventRateParameter(SEPAInvocationGraph graph, int avgRate, int outputRate, String outputName) {
		super(graph);
		this.avgRate = avgRate;
		this.outputRate = outputRate;
		this.outputName = outputName;
	}

	public int getAvgRate() {
		return avgRate;
	}

	public void setAvgRate(int avgRate) {
		this.avgRate = avgRate;
	}

	public int getOutputRate() {
		return outputRate;
	}

	public void setOutputRate(int outputRate) {
		this.outputRate = outputRate;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	
	
	
}
