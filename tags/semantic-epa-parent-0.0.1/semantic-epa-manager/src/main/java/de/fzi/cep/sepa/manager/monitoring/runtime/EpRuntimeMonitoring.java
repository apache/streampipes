package de.fzi.cep.sepa.manager.monitoring.runtime;


public interface EpRuntimeMonitoring<T> {
	
	public abstract boolean register(PipelineObserver observer);
	public abstract boolean remove(PipelineObserver observer);

}
