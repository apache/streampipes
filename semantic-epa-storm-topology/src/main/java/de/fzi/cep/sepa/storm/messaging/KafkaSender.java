package de.fzi.cep.sepa.storm.messaging;

import java.io.IOException;
import java.util.Map;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import de.fzi.cep.sepa.storm.utils.Constants;
import de.fzi.cep.sepa.storm.utils.Serializer;

public class KafkaSender<B extends BindingParameters> implements EPEngine<B>{

	private ProaSenseInternalProducer kafkaDataProducer;
	private ProaSenseInternalProducer kafkaConfigProducer;
	private String configurationId;
	
	public KafkaSender(String producerKafkaUrl, String producerTopic, String configurationId)
	{
		this.kafkaDataProducer = new ProaSenseInternalProducer(producerKafkaUrl, producerTopic +  Constants.SEPA_DATA);
		this.kafkaConfigProducer = new ProaSenseInternalProducer(producerKafkaUrl, producerTopic +  Constants.SEPA_CONFIG);
		this.configurationId = configurationId;
	}

	@Override
	public void bind(EngineParameters<B> parameters, OutputCollector collector,
			SepaInvocation graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		try {
			event.put("configurationId", configurationId);
			kafkaDataProducer.send(Serializer.serialize(event));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void discard() {
		ConfigurationMessage<B> config = new ConfigurationMessage<>(Operation.DETACH, configurationId, null);
		try {
			System.out.println("discarding");
			kafkaConfigProducer.send(Serializer.serialize(config));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
