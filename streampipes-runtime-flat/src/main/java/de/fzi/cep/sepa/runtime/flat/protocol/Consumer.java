package de.fzi.cep.sepa.runtime.flat.protocol;

import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;

import java.util.HashMap;
import java.util.Map;

public abstract class Consumer<T> implements EventListener<T> {

	protected Map<String, ConsumerMessageListener> listeners;
	protected DatatypeDefinition dataType;
	
	public Consumer(DatatypeDefinition dataType) {
		this.listeners = new HashMap<>();
		this.dataType = dataType;
	}
	
	public void addListener(String routeId, ConsumerMessageListener listener) {
		listeners.put(routeId, listener);
	}
	
	public void removeListener(String routeId) {
		listeners.remove(routeId);
	}
	
	public void notify(Map<String, Object> event) {
		listeners.entrySet().forEach(l -> l.getValue().onEvent(event));
	}
	
	public int getCurrentListenerCount() {
		return listeners.size();
	}
	
	public abstract void openConsumer();
	
	public abstract void closeConsumer();
	
}
