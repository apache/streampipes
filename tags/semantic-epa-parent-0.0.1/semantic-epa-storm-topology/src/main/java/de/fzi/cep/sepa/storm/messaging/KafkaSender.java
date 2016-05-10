package de.fzi.cep.sepa.storm.messaging;

import java.io.IOException;
import java.util.Map;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.storm.utils.Serializer;


//TODO Do I need this class?
public class KafkaSender<B extends BindingParameters> implements EPEngine<B>{

	private ProaSenseInternalProducer kafkaDataProducer;
	private ProaSenseInternalProducer kafkaConfigProducer;
	private String configurationId;
	
	public KafkaSender(String producerKafkaUrl, String producerTopic, String configurationId)
	{
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
		//TODO
	}

	
}
