package de.fzi.cep.sepa.runtime.flat.protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class Consumer {

	protected Map<String, IMessageListener> listeners;
	
	public Consumer() {
		this.listeners = new HashMap<>();
	}
	
	public void addListener(String routeId, IMessageListener listener) {
		listeners.put(routeId, listener);
	}
	
	public void removeListener(String routeId) {
		listeners.remove(routeId);
	}
	
	public void notify(String event) {
		listeners.entrySet().forEach(l -> l.getValue().onEvent(event));
	}
	
	public int getCurrentListenerCount() {
		return listeners.size();
	}
	
	public abstract void openConsumer();
	
	public abstract void closeConsumer();
	
}
