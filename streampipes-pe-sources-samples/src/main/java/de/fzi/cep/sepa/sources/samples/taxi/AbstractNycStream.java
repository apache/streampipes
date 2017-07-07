package de.fzi.cep.sepa.sources.samples.taxi;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;

import java.io.File;

public abstract class AbstractNycStream implements EventStreamDeclarer{

	protected EventProducer publisher;
	protected EventProducer timePublisher;
	
	public AbstractNycStream(String topic) {
		publisher = NycTaxiUtils.streamPublisher(topic);
		timePublisher = NycTaxiUtils.streamPublisher("FZI.Timer");
	}
	
	public void executeReplay(File file) {
		new Thread(new TaxiStreamGenerator(file, SimulationSettings.DEMONSTRATE_10, publisher)).start();
	}

	
}
