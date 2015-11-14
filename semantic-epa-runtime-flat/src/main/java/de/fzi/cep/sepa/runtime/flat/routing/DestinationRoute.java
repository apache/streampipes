package de.fzi.cep.sepa.runtime.flat.routing;

import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;
import de.fzi.cep.sepa.runtime.routing.EPConsumer;

public class DestinationRoute extends Route implements EPConsumer {

	private Producer producer;
	private int counter = 0;
	
	public DestinationRoute(String topic, String routeId, DatatypeDefinition dataType, Producer producer, OutputCollector collector) {
		super(routeId, dataType, topic);
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
		producer.onEvent(dataType.marshal(event));
		counter++;
		if (counter % 1000 == 0) System.out.println(counter +" events processed.");
	}


}
