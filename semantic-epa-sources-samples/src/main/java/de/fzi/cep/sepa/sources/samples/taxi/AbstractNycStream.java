package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.File;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.sources.samples.activemq.IMessagePublisher;

public abstract class AbstractNycStream implements EventStreamDeclarer{

	protected IMessagePublisher publisher;
	protected IMessagePublisher timePublisher;
	
	public AbstractNycStream(String topic) {
		publisher = NycTaxiUtils.streamPublisher(topic);
		timePublisher = NycTaxiUtils.streamPublisher("FZI.Timer");
	}
	
	public void executeReplay(File file) {
		new Thread(new TaxiStreamGenerator(file, SimulationSettings.PERFORMANCE_TEST, publisher, timePublisher)).start();
	}

	
}
