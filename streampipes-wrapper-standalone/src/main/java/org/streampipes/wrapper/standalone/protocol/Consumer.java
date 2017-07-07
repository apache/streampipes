package org.streampipes.wrapper.standalone.protocol;

import org.streampipes.messaging.EventListener;
import org.streampipes.wrapper.standalone.datatype.DatatypeDefinition;

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
