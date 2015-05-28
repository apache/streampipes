package de.fzi.cep.sepa.actions.test;

import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseConfig;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseInternalProducer;

public class TestKafkaProducer {

	public static void main(String[] args)
	{
		ProaSenseInternalProducer producer = new ProaSenseInternalProducer(ProaSenseConfig.BROKER_URL, "eu.proasense.internal.sp.internal.outgoing.10000");
		producer.send("abc".getBytes());
	}

}
