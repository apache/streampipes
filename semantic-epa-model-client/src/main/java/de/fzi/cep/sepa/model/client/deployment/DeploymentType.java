package de.fzi.cep.sepa.model.client.deployment;


public enum DeploymentType {
	SEPA_STORM, 
	SEPA_ESPER, 
	SEPA_ALGORITHM, 
	SEPA_FLINK, 
	ACTION, 
	ACTION_FLINK, 
	SEP;
}
