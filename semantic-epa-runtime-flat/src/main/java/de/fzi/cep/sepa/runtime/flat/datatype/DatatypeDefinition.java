package de.fzi.cep.sepa.runtime.flat.datatype;

import java.io.Serializable;
import java.util.Map;

public interface DatatypeDefinition extends Serializable {

	Map<String, Object> unmarshal(byte[] input);
	
	byte[] marshal(Object event);
	
}
