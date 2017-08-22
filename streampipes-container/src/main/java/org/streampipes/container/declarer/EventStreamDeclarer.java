package org.streampipes.container.declarer;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;


public interface EventStreamDeclarer {

	EventStream declareModel(SepDescription sep);
	
	void executeStream();
	
	boolean isExecutable();
}
