package org.streampipes.pe.sources.samples.twitter;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.quality.EventPropertyQualityDefinition;
import org.streampipes.model.impl.quality.EventStreamQualityDefinition;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.SampleSettings;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;
import org.codehaus.jettison.json.JSONObject;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterSampleStream implements EventStreamDeclarer {

	private ActiveMQPublisher geoPublisher;
	private EventProducer kafkaProducer;

	public TwitterSampleStream() throws JMSException {
		geoPublisher = new ActiveMQPublisher(ClientConfiguration.INSTANCE.getJmsHost() + ":61616", "SEPA.SEP.Twitter.Geo");
		kafkaProducer = new SpKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), "SEPA.SEP.Twitter.Sample");
	}

	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();
		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> timestampQualities = new ArrayList<EventPropertyQualityDefinition>();
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(PrimitivePropertyBuilder.createProperty(XSD._string, "content", SO.Text).build());
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				Utils.createURI("http://test.de/timestamp"), timestampQualities));

		EventPropertyPrimitive userName = new EventPropertyPrimitive(XSD._string.toString(), "userName", "",
				Utils.createURI("http://foaf/name"));
		EventPropertyPrimitive followerCount = new EventPropertyPrimitive(XSD._integer.toString(), "followers", "",
				Utils.createURI(SO.Number));
		List<EventProperty> userProperties = new ArrayList<>();
		userProperties.add(userName);
		userProperties.add(followerCount);

		//eventProperties.add(new EventPropertyNested("user", userProperties));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(10));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.kafkaProtocol("SEPA.SEP.Twitter.Sample"));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);
		stream.setName("Twitter Sample Stream");
		stream.setDescription("Twitter Sample Stream Description");
		stream.setUri(sep.getUri() + "/sample");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Tweet_Icon" + "_HQ.png");

		return stream;
	}

	@Override
	public void executeStream() {
		twitter4j.TwitterStream twitterStream;
		ConfigurationBuilder cb;

		cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey("hON6DefSppNQk2NOJ9pZ0A");
		cb.setOAuthConsumerSecret("1qPFRX4bUW4qEci2RPVx7muPgy7aY2E8iRzQXrgME");
		cb.setOAuthAccessToken("74137491-xrIoFunaCEGZbjYqttx3VC2BS7cNcXRPYsZs2foep");
		cb.setOAuthAccessTokenSecret("RWvytKLDRQzpPSlnwnYx80JnSxP7Xmpc3zf48U6JnCc");

		
		StatusListener listener = new StatusListener() {
			
			int counter = 0;
			
			public void onStatus(Status status) {
					counter++;
					kafkaProducer.publish(buildJson(status).toString().getBytes());
					if (counter % 100 == 0) System.out.println(counter +" Events (Twitter Sample Stream) sent.");
				if (status.getGeoLocation() != null) {
					try {

						geoPublisher.sendText(buildGeoJson(status).toString());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				System.out.println(arg0.getMessage());
				System.out.println(arg0.getPercentFull());

			}
		};

		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		twitterStream.addListener(listener);
		twitterStream.sample();

	}

	public JSONObject buildJson(Status status) {
		JSONObject json = new JSONObject();

		try {
			json.put("timestamp", status.getCreatedAt().getTime());
			JSONObject user = new JSONObject();
			json.put("userName", status.getUser().getName());
			json.put("followers", status.getUser().getFollowersCount());
			//json.put("user", user);
			json.put("content", status.getText());
			// json.put("name", "TwitterEvent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return json;
	}

	public JSONObject buildGeoJson(Status status) {
		JSONObject json = new JSONObject();

		try {
			json.put("latitude", status.getGeoLocation().getLatitude());
			json.put("longitude", status.getGeoLocation().getLongitude());
			json.put("timestamp", status.getCreatedAt().getTime());
			json.put("userName", status.getUser().getName());
			json.put("text", status.getText());
			// json.put("name", "TwitterGeoEvent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return json;
	}
	
	private SimpleEvent buildSimpleEvent(long timestamp, String username, int follower, String content) {
		Map<String, ComplexValue> map = new HashMap<String, ComplexValue>();
		ComplexValue value = new ComplexValue();
		value.setType(VariableType.LONG);
		value.setValue(String.valueOf(timestamp));

		ComplexValue value2 = new ComplexValue();
		value2.setType(VariableType.STRING);
		value2.setValue(String.valueOf(username));

		ComplexValue value3 = new ComplexValue();
		value3.setType(VariableType.LONG);
		value3.setValue(String.valueOf(follower));
		
		ComplexValue value4 = new ComplexValue();
		value4.setType(VariableType.STRING);
		value4.setValue(String.valueOf(content));

		map.put("timestamp", value);
		map.put("username", value2);
		map.put("follower", value3);
		map.put("content", value3);
		SimpleEvent simpleEvent = new SimpleEvent(timestamp, "SampleStream", map);
		simpleEvent.setSensorId("SampleStream");
		return simpleEvent;
	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return true;
	}

}
