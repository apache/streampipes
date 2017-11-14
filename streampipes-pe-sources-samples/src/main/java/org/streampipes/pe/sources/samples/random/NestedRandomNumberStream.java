package org.streampipes.pe.sources.samples.random;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NestedRandomNumberStream implements EventStreamDeclarer {
	
	ActiveMQPublisher samplePublisher;

	public NestedRandomNumberStream() throws JMSException
	{
		samplePublisher = new ActiveMQPublisher(SourcesConfig.INSTANCE.getJmsHost() +":61616", "SEPA.SEP.Random.SimpleNestedNumber");
	}
	
	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		
		SpDataStream stream = new SpDataStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "", Utils.createURI("http://schema.org/Number")));
		
		EventProperty listA = new EventPropertyPrimitive(XSD._integer.toString(), "someRandomNumber2", "", Utils.createURI("http://schema.org/Number"));
		EventProperty listB = new EventPropertyPrimitive(XSD._string.toString(), "someRandomText2", "", Utils.createURI("http://test.de/text"));
		
		EventProperty nestedList = new EventPropertyNested("values", Utils.createList(listA, listB));
		
		eventProperties.add(nestedList);
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Random.SimpleNestedNumber"));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Nested Random Number Stream");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/number/simplenested");
		
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
		// TODO Auto-generated method stub
		return true;
	}
	
	private JSONObject buildJson(long timestamp, int number) 
	{
		Random random = new Random();
		JSONObject json = new JSONObject();
		
			
			JSONObject object = new JSONObject();
			
			try {
				object.put("someRandomText2", "abc");
				object.put("someRandomNumber", random.nextInt(100));
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		try {
			json.put("timestamp", timestamp);
			json.put("randomValue", number);
			json.put("values", object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}

}
