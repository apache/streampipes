package org.streampipes.runtime.flat.protocol;

import java.util.Map;

public interface ConsumerMessageListener {

	void onEvent(Map<String, Object> event);
}
