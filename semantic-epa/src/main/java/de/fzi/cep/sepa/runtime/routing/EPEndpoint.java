package de.fzi.cep.sepa.runtime.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import de.fzi.cep.sepa.runtime.EPRuntime;

public class EPEndpoint extends DefaultEndpoint {

	private final EPRuntime runtime;

	public EPEndpoint(EPRuntime runtime, String uri, CamelContext context) {
		this.runtime = runtime;
		this.setEndpointUri(uri);
		this.setCamelContext(context);
	}

	@Override
	public Producer createProducer() throws Exception {
		return new EPProducer(this, runtime.getEngine());
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new EPConsumer(this, processor, runtime.getOutputCollector());
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
