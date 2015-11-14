package de.fzi.cep.sepa.runtime.flat.datatype;

import java.util.Map;

public interface DatatypeDefinition {

	public Map<String, Object> unmarshal(String input);
	
	public String marshal(Object event);
}
