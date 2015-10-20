package de.fzi.cep.sepa.actions.samples.kafka;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;

public class KafkaPublisher implements IMessageListener {

	private ProaSenseInternalProducer producer;
	private String pipelineId;
	private JsonParser jsonParser;
	
	public KafkaPublisher(ProaSenseInternalProducer producer, String pipelineId)
	{
		this.producer = producer;
		this.pipelineId = pipelineId;
		this.jsonParser = new JsonParser();
	}
	
	@Override
	public void onEvent(String message) {
		JsonObject jsonObj = (JsonObject) jsonParser.parse(message);
		if (pipelineId != null) jsonObj.addProperty("pipelineId", pipelineId);
		producer.send(jsonObj.toString().getBytes());
	}

	public static void main(String[] args)
	{
		String json = "{\"timestamp\":1441899506000}";
		new KafkaPublisher(null, "abc").onEvent(json);;
	}
}
