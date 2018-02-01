package org.streampipes.manager.monitoring.runtime;

import java.util.Map;

public interface FormatGenerator {

	Object makeOutputFormat(Map<String, Object> event);
	
}
