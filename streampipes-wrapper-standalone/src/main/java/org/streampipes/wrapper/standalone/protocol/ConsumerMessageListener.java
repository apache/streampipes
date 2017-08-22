package org.streampipes.wrapper.standalone.protocol;

import java.util.Map;

public interface ConsumerMessageListener {

	void onEvent(Map<String, Object> event);
}
