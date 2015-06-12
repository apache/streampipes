package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;

public interface OutputSchemaGenerator<T extends OutputStrategy> {

	public EventSchema buildFromOneStream(EventStream stream);
	
	public EventSchema buildFromTwoStreams(EventStream stream1, EventStream stream2);
	
	public OutputStrategy getModifiedOutputStrategy(T outputStrategy);
}
