package de.fzi.cep.sepa.manager.monitoring.runtime;

import de.fzi.cep.sepa.model.AbstractSEPAElement;

public interface EpRuntimeMonitoring<T> {
	
	public abstract boolean register(String pipelineId);
	public abstract boolean remove(String pipelineId);

}
