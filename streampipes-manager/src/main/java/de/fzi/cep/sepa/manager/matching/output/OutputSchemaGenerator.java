package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public interface OutputSchemaGenerator<T extends OutputStrategy> {

	EventSchema buildFromOneStream(EventStream stream);
	
	EventSchema buildFromTwoStreams(EventStream stream1, EventStream stream2);
	
	OutputStrategy getModifiedOutputStrategy(T outputStrategy);
}
