package de.fzi.cep.sepa.model.impl;

public class OutputStrategy<T extends OutputStrategyParameter> {

	OutputStrategyType outputStrategyType;
	T outputStrategyParameter;
	
	public OutputStrategy(OutputStrategyType type, T outputStrategyParameter)
	{
		this.outputStrategyParameter = outputStrategyParameter;
		this.outputStrategyType = type;
	}

	public OutputStrategyType getOutputStrategyType() {
		return outputStrategyType;
	}

	public void setOutputStrategyType(OutputStrategyType outputStrategyType) {
		this.outputStrategyType = outputStrategyType;
	}

	public T getOutputStrategyParameter() {
		return outputStrategyParameter;
	}

	public void setOutputStrategyParameter(T outputStrategyParameter) {
		this.outputStrategyParameter = outputStrategyParameter;
	}
	
	
}
