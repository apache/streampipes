package de.fzi.cep.sepa.runtime.camel.routing;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.routing.EPConsumer;
import de.fzi.cep.sepa.runtime.routing.Timer;

public class CamelEPConsumer extends DefaultConsumer implements EPConsumer {

	private final Endpoint endpoint;

	private final OutputCollector collector;
	private static int counter = 0;

	public CamelEPConsumer(Endpoint endpoint, Processor processor, OutputCollector collector) {
		super(endpoint, processor);
		this.endpoint = endpoint;
		this.collector = collector;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		collector.addListener(this);
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		collector.removeListener(this);
	}

	@Override
	public void accept(Object event) {
		Exchange exchange = endpoint.createExchange(ExchangePattern.InOnly);
		exchange.getIn().setBody(event);
		exchange.getIn().getHeaders().put("kafka.PARTITION_KEY", "4");
		if (counter == 190000) Timer.stop();
		counter++;
		try {
			getProcessor().process(exchange);
		} catch (Exception e) {
			getExceptionHandler().handleException("Cannot update event", exchange, e);
		}
	}
}
