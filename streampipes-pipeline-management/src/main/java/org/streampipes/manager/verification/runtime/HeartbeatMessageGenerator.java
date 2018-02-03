package org.streampipes.manager.verification.runtime;

import org.streampipes.model.graph.DataProcessorDescription;

public class HeartbeatMessageGenerator {

	private DataProcessorDescription description;
	
	public HeartbeatMessageGenerator(DataProcessorDescription description)
	{
		this.description = description;
	}
	
	
}
