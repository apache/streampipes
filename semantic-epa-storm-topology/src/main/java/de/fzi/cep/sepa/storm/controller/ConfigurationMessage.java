package de.fzi.cep.sepa.storm.controller;

import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class ConfigurationMessage<B extends BindingParameters> implements Serializable {

	private static final long serialVersionUID = -3689713033832178434L;
	
	private Operation operation;
	private String configurationId;
	private B bindingParameters;
	
	public ConfigurationMessage() {
		
	}
	
	public ConfigurationMessage(Operation operation, B bindingParameters)
	{
		this.operation = operation;
		this.bindingParameters = bindingParameters;
		this.configurationId = RandomStringUtils.randomAlphanumeric(10);
	}
	
	public ConfigurationMessage(Operation operation, String configurationId, B bindingParameters)
	{
		this.operation = operation;
		this.bindingParameters = bindingParameters;
		this.configurationId = configurationId;
	}

	public Operation getOperation() {
		return operation;
	}

	public B getBindingParameters() {
		return bindingParameters;
	}

	public String getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
	
	
	
	
}
