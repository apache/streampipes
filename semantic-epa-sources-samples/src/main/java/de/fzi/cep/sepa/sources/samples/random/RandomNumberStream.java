package de.fzi.cep.sepa.sources.samples.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.jms.JMSException;

import org.codehaus.jettison.json.JSONObject;
import de.fzi.cep.sepa.model.vocabulary.XSD;

import twitter4j.Status;
import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class RandomNumberStream implements EventStreamDeclarer {

	ActiveMQPublisher samplePublisher;

	public RandomNumberStream() throws JMSException
	{
		samplePublisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "SEPA.SEP.Random.Number");
	}
	
	@Override
	public EventStream declareModel(SEP sep) {
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomString", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri(Configuration.TCP_SERVER_URL);
		grounding.setTopicName("SEPA.SEP.Random.Number");
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Random Number Stream");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/number");
		
		return stream;
	}

	@Override
	public void executeStream() {
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				Random random = new Random();
				for(;;)
				{
					try {
						String json = buildJson(System.currentTimeMillis(), random.nextInt(100)).toString();
						System.out.println(json);
						samplePublisher.sendText(json);
						Thread.sleep(1000);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();
		

	}

	@Override
	public boolean isExecutable() {
		return true;
	}
	
	private JSONObject buildJson(long timestamp, int number)
	{
		JSONObject json = new JSONObject();
		
		try {
			json.put("timestamp", timestamp);
			json.put("randomValue", number);
			json.put("randomString", randomString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}
	
	private String randomString()
	{
		String[] randomStrings = new String[] {"a", "b", "c", "d"};
		Random random = new Random();
		return randomStrings[random.nextInt(3)];
	}

}
