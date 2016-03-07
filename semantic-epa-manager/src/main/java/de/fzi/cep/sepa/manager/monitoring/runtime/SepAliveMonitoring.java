package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.model.impl.graph.SepDescription;

/**
 * Run the monitring class
 * Register sources and pipeline to monitor (Just one monitor per source even when in multiple pipelines)
 * Add Kafka listener on one specific topic and print when source stopped
 *
 */
public class SepAliveMonitoring implements EpRuntimeMonitoring<SepDescription>, Runnable {

	private Map<SepDescription, List<String>> monitoredPipelines;
	
	
	
	@Override
	public boolean register(String pipelineId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(String pipelineId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void run() {
		monitoredPipelines = new HashMap<>();
		
	}

	

}
