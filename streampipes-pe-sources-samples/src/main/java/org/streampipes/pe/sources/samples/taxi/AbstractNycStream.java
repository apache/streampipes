package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;

import java.io.File;

public abstract class AbstractNycStream implements DataStreamDeclarer {

	protected SpKafkaProducer publisher;
	protected EventProducer timePublisher;
	
	public AbstractNycStream(String topic) {
		publisher = NycTaxiUtils.streamPublisher(topic);
		timePublisher = NycTaxiUtils.streamPublisher("FZI.Timer");
	}
	
	public void executeReplay(File file) {
		new Thread(new TaxiStreamGenerator(file, SimulationSettings.DEMONSTRATE_10, publisher)).start();
	}

	
}
