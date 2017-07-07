package org.streampipes.wrapper.standalone.routing;

import org.streampipes.wrapper.OutputCollector;
import org.streampipes.wrapper.standalone.protocol.Producer;
import org.streampipes.wrapper.routing.EPConsumer;

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
