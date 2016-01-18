package de.fzi.cep.sepa.runtime.flat.protocol;

import java.util.Map;

public interface ConsumerMessageListener {

	public void onEvent(Map<String, Object> event);
}
