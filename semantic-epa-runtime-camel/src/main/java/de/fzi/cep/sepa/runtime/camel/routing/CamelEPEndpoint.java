package de.fzi.cep.sepa.runtime.camel.routing;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import de.fzi.cep.sepa.runtime.camel.CamelEPRuntime;

public class CamelEPEndpoint extends DefaultEndpoint {

	private final CamelEPRuntime runtime;

	public CamelEPEndpoint(CamelEPRuntime runtime, String uri, CamelContext context) {
		this.runtime = runtime;
		this.setEndpointUri(uri);
		this.setCamelContext(context);
	}

	@Override
	public Producer createProducer() throws Exception {
		return new CamelEPProducer(this, runtime.getEngine());
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		return new CamelEPConsumer(this, processor, runtime.getOutputCollector());
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
