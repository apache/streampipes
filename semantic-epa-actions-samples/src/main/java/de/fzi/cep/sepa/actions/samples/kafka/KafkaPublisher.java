package de.fzi.cep.sepa.actions.samples.kafka;

import org.json.JSONObject;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;

public class KafkaPublisher implements IMessageListener {

	private ProaSenseInternalProducer producer;
	private String pipelineId;
	
	public KafkaPublisher(ProaSenseInternalProducer producer, String pipelineId)
	{
		this.producer = producer;
		this.pipelineId = pipelineId;
	}
	
	@Override
	public void onEvent(String message) {
		JSONObject jsonObject = new JSONObject(message);
		if (pipelineId != null) jsonObject = jsonObject.append("pipelineId", pipelineId);
		producer.send(jsonObject.toString().getBytes());
	}

	public static void main(String[] args)
	{
		String json = "{\"timestamp\":1441899506000}";
		new KafkaPublisher(null, "abc").onEvent(json);;
	}
}
