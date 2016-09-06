package de.fzi.cep.sepa.runtime.flat.datatype;

import java.util.Map;

public interface DatatypeDefinition {

	Map<String, Object> unmarshal(byte[] input);
	
	byte[] marshal(Object event);
	
}
