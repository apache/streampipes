package de.fzi.cep.sepa.manager.matching.output;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;

public interface OutputSchemaGenerator {

	public EventSchema buildFromOneStream(EventStream stream);
	
	public EventSchema buildFromTwoStreams(EventStream stream1, EventStream stream2);
}
