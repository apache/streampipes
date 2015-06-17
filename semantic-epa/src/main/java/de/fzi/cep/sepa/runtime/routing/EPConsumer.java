package de.fzi.cep.sepa.runtime.routing;

import java.util.function.Consumer;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import de.fzi.cep.sepa.runtime.OutputCollector;

public class EPConsumer extends DefaultConsumer implements Consumer<Object> {

	private final Endpoint endpoint;

	private final OutputCollector collector;

	public EPConsumer(Endpoint endpoint, Processor processor, OutputCollector collector) {
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
		try {
			getProcessor().process(exchange);
		} catch (Exception e) {
			getExceptionHandler().handleException("Cannot update event", exchange, e);
		}
	}
}
