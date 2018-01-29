package org.streampipes.manager.monitoring.runtime;


public interface EpRuntimeMonitoring<T> {
	
	boolean register(PipelineObserver observer);
	boolean remove(PipelineObserver observer);

}
