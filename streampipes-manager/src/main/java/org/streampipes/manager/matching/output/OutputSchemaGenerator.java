package org.streampipes.manager.matching.output;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.OutputStrategy;

public interface OutputSchemaGenerator<T extends OutputStrategy> {

	EventSchema buildFromOneStream(SpDataStream stream);
	
	EventSchema buildFromTwoStreams(SpDataStream stream1, SpDataStream stream2);
	
	OutputStrategy getModifiedOutputStrategy(T outputStrategy);
}
