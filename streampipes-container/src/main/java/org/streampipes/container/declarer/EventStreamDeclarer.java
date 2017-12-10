package org.streampipes.container.declarer;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;


public interface EventStreamDeclarer {

	SpDataStream declareModel(DataSourceDescription sep);
	
	void executeStream();
	
	boolean isExecutable();
}
