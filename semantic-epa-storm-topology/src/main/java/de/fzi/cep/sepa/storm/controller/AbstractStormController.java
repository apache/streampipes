package de.fzi.cep.sepa.storm.controller;


import java.io.IOException;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.messaging.KafkaSender;
import de.fzi.cep.sepa.storm.utils.Constants;
import de.fzi.cep.sepa.storm.utils.Serializer;

public abstract class AbstractStormController<B extends BindingParameters> extends EpDeclarer<B> {
	
	
	protected Response prepareTopology(ConfigurationMessage<B> params) {
		String topicName = Constants.SEPA_PREFIX;// +params.getConfigurationId();
		ProaSenseInternalProducer producer = new ProaSenseInternalProducer(getKafkaUrl(), topicName +Constants.SEPA_CONFIG);
		try {
			producer.send(Serializer.serialize(params));
			invokeEPRuntime(params.getBindingParameters(), () -> new KafkaSender<B>(getKafkaUrl(), topicName, params.getConfigurationId()), params.getBindingParameters().getGraph());	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return new Response(params.getBindingParameters().getGraph().getElementId(), true);
		
	}
	
	protected abstract String getKafkaUrl();
	

}
