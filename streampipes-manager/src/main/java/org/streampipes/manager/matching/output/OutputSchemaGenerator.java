package org.streampipes.manager.matching.output;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.output.OutputStrategy;

public interface OutputSchemaGenerator<T extends OutputStrategy> {

	EventSchema buildFromOneStream(EventStream stream);
	
	EventSchema buildFromTwoStreams(EventStream stream1, EventStream stream2);
	
	OutputStrategy getModifiedOutputStrategy(T outputStrategy);
}
