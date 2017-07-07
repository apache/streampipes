package org.streampipes.wrapper;

import org.streampipes.wrapper.routing.EPConsumer;

import java.util.ArrayList;
import java.util.List;

public class OutputCollector {

	private final List<EPConsumer> listeners; // TODO concurrency

	public OutputCollector() {
		this.listeners = new ArrayList<>();
	}

	public void send(Object event) {
		listeners.forEach(l -> l.accept(event));
	}

	public void addListener(EPConsumer epConsumer) {
		listeners.add(epConsumer);
	}

	public void removeListener(EPConsumer epConsumer) {
		listeners.remove(epConsumer);
	}
}
