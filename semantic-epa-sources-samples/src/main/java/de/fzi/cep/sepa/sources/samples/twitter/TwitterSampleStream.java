package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.codehaus.jettison.json.JSONObject;

import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSampleStream implements EventStreamDeclarer {
	
	ActiveMQPublisher samplePublisher;
	ActiveMQPublisher geoPublisher;
	
	public TwitterSampleStream() throws JMSException
	{
		samplePublisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "SEPA.SEP.Twitter.Sample");
		geoPublisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "SEPA.SEP.Twitter.Geo");
	}
	
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		EventSchema schema = new EventSchema();
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		eventProperties.add(PrimitivePropertyBuilder.createProperty(XSD._string, "text", SO.Text).build());
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		
		EventPropertyPrimitive userName = new EventPropertyPrimitive(XSD._string.toString(), "userName", "", de.fzi.cep.sepa.commons.Utils.createURI("http://foaf/name"));
		EventPropertyPrimitive followerCount = new EventPropertyPrimitive(XSD._integer.toString(), "followers", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		List<EventProperty> userProperties = new ArrayList<>();
		userProperties.add(userName);
		userProperties.add(followerCount);
		
		eventProperties.add(new EventPropertyNested("user", userProperties));
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Twitter.Geo"));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Twitter Sample Stream");
		stream.setDescription("Twitter Sample Stream Description");
		stream.setUri(sep.getUri() + "/sample");
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Tweet_Icon" +"_HQ.png");
		
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
		
	
		 StatusListener listener = new StatusListener(){
		        public void onStatus(Status status) {
		        	try {
						samplePublisher.sendText(buildJson(status).toString());
					} catch (JMSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	if (status.getGeoLocation() != null)
		        	{
			            try {
			    
							geoPublisher.sendText(buildGeoJson(status).toString());
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
		        }
		        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
		        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
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
	
	public JSONObject buildJson(Status status)
	{
		JSONObject json = new JSONObject();
		
		try {
			json.put("timestamp", status.getCreatedAt().getTime());
			JSONObject user = new JSONObject();
			user.put("userName", status.getUser().getName());
			user.put("follower", status.getUser().getFollowersCount());
			json.put("user", user);
			json.put("text", status.getText());
			//json.put("name", "TwitterEvent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}
	
	public JSONObject buildGeoJson(Status status)
	{
		JSONObject json = new JSONObject();
		
		try {
			json.put("latitude", status.getGeoLocation().getLatitude());
			json.put("longitude", status.getGeoLocation().getLongitude());
			json.put("timestamp", status.getCreatedAt().getTime());
			json.put("userName", status.getUser().getName());
			json.put("text", status.getText());
			//json.put("name", "TwitterGeoEvent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}


	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return true;
	}

}
