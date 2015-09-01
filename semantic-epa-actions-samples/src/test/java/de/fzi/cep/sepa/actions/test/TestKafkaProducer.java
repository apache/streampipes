package de.fzi.cep.sepa.actions.test;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;

public class TestKafkaProducer {

	public static void main(String[] args)
	{
		ProaSenseInternalProducer producer = new ProaSenseInternalProducer(Configuration.getInstance().getBrokerConfig().getKafkaUrl(), "eu.proasense.internal.sp.internal.outgoing.10000");
		producer.send("abc".getBytes());
		producer.shutdown();
	}

}
