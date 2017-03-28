package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.Map;

public interface FormatGenerator {

	Object makeOutputFormat(Map<String, Object> event);
	
}
