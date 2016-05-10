package de.fzi.cep.sepa.runtime.flat.routing;

import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;
import de.fzi.cep.sepa.runtime.routing.EPConsumer;

public class DestinationRoute extends Route implements EPConsumer {

	private Producer producer;
	private int counter = 0;
	
	public DestinationRoute(String topic, String routeId, Producer producer, OutputCollector collector) {
		super(routeId, topic);
		collector.addListener(this);
		this.producer = producer;
	}

	@Override
	public void startRoute() {
		
	}

	@Override
	public void stopRoute() {
		
	}

	@Override
	public void accept(Object event) {
		producer.publish(event);
		counter++;
		if (counter % 10000 == 0) System.out.println(counter +" events processed (topic " +topic +")");
	}


}
