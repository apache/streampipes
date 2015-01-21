package de.fzi.cep.sepa.runtime.param;

import java.util.List;

public abstract class BindingParameters {

	protected String inName;
	protected String outName;
	
	protected String inputTopic;
	protected String outputTopic;
	
	protected List<String> allProperties;
	protected List<String> partitionProperties;
	
	public BindingParameters(String inName, String outName, List<String> allProperties, List<String> partitionProperties)
	{
		this.inName = inName;
		this.outName = outName;
		this.allProperties = allProperties;
		this.partitionProperties = partitionProperties;
	}

	public String getInName() {
		return inName;
	}

	public String getOutName() {
		return outName;
	}

	public List<String> getAllProperties() {
		return allProperties;
	}

	public List<String> getPartitionProperties() {
		return partitionProperties;
	}
	
	
}
