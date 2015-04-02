package de.fzi.cep.sepa.actions.samples.heatmap;

import javax.jms.JMSException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQConsumer;
import de.fzi.cep.sepa.actions.messaging.jms.ActiveMQPublisher;
import de.fzi.cep.sepa.actions.messaging.jms.IMessageListener;

public class HeatmapListener implements Runnable, IMessageListener{

	private JsonArray jsonArray = new JsonArray();
	private JsonParser parser;
	private String brokerUrl;
	private String sourceTopic;
	private ActiveMQPublisher publisher;
	private int counter = 0;
	
	public HeatmapListener(String brokerUrl, String destinationTopic, String sourceTopic)
	{
		this.brokerUrl = brokerUrl;
		this.sourceTopic = sourceTopic;
		this.parser = new JsonParser();
		try {
			publisher = new ActiveMQPublisher(brokerUrl, destinationTopic);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEvent(String json) {
		counter++;
		JsonElement jsonElement = parser.parse(json);
		JsonObject obj = jsonElement.getAsJsonObject();
		jsonArray.add(obj);
		if (counter > 300) sendAndReset();
	}
	
	private void sendAndReset()
	{
		send(jsonArray);
		jsonArray = new JsonArray();
		counter = 0;
	}
	
	private void send(JsonArray array)
	{
		publisher.send(array.toString());
	}

	@Override
	public void run() {
		ActiveMQConsumer consumer = new ActiveMQConsumer(brokerUrl, sourceTopic);
		consumer.setListener(this);
	}
	
	

}
