package de.fzi.cep.sepa.model.client;

public class OutputStrategy<T extends OutputStrategyParameters> {

	OutputStrategyType outputStrategyType;
	T outputStrategyParameters;
	
	public OutputStrategy(OutputStrategyType type, T outputStrategyParameters)
	{
		this.outputStrategyParameters = outputStrategyParameters;
		this.outputStrategyType = type;
	}

	public OutputStrategyType getOutputStrategyType() {
		return outputStrategyType;
	}

	public void setOutputStrategyType(OutputStrategyType outputStrategyType) {
		this.outputStrategyType = outputStrategyType;
	}

	public T getOutputStrategyParameters() {
		return outputStrategyParameters;
	}

	public void setOutputStrategyParameters(T outputStrategyParameters) {
		this.outputStrategyParameters = outputStrategyParameters;
	}
	
	
}
