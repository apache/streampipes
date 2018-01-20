package org.streampipes.container.declarer;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;


public interface DataStreamDeclarer {

	SpDataStream declareModel(DataSourceDescription sep);
	
	void executeStream();
	
	boolean isExecutable();
}
