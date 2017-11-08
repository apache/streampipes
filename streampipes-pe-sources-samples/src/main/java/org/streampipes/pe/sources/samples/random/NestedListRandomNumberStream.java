package org.streampipes.pe.sources.samples.random;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NestedListRandomNumberStream implements EventStreamDeclarer {
	
	ActiveMQPublisher samplePublisher;

	public NestedListRandomNumberStream() throws JMSException
	{
		samplePublisher = new ActiveMQPublisher(SourcesConfig.INSTANCE.getJmsHost() +":61616", "SEPA.SEP.Random.NestedNumber");
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "", Utils.createURI("http://schema.org/Number")));
		
		EventProperty primitiveList = new EventPropertyPrimitive(XSD._integer.toString(), "someRandomNumber", "", Utils.createURI("http://schema.org/Number"));
		
		EventProperty listProperty = new EventPropertyList("someNumbers", primitiveList);
		eventProperties.add(listProperty);
		
		EventProperty listA = new EventPropertyPrimitive(XSD._integer.toString(), "someRandomNumber2", "", Utils.createURI("http://schema.org/Number"));
		EventProperty listB = new EventPropertyPrimitive(XSD._string.toString(), "someRandomText2", "", Utils.createURI("http://test.de/text"));
		
		EventProperty nestedList = new EventPropertyNested("values", Utils.createList(listA, listB));
		
		EventProperty nestedListProperty = new EventPropertyList("nestedNumbers", nestedList);
		eventProperties.add(nestedListProperty);
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Random.NestedNumber"));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Nested List Random Number Stream");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/number/nested");
		
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
						e.printStackTrace();
					} catch (InterruptedException e) {
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
		Random random = new Random();
		JSONObject json = new JSONObject();
		
		JSONArray array = new JSONArray();
		for(int i = 0; i < 5; i++)
		{
			array.put(random.nextInt(100));
		}
		
		JSONArray listedNumbers = new JSONArray();
		for(int i = 0; i < 6; i++)
		{
			
			JSONObject object = new JSONObject();
			
			try {
				object.put("someRandomText2", "abc");
				object.put("someRandomNumber", random.nextInt(100));
				JSONObject values = new JSONObject();
				values.put("values", object);
				listedNumbers.put(values);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
		
		try {
			json.put("timestamp", timestamp);
			json.put("randomValue", number);
			json.put("someNumbers", array);
			json.put("nestedNumbers", listedNumbers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}

}
