package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.codehaus.jettison.json.JSONObject;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
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
		samplePublisher = new ActiveMQPublisher("tcp://localhost:61616", "SEPA.SEP.Twitter.Sample");
		geoPublisher = new ActiveMQPublisher("tcp://localhost:61616", "SEPA.SEP.Twitter.Geo");
	}
	
	
	@Override
	public EventStream declareModel(SEP sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventProperty(XSD._string.toString(), "text", ""));
		eventProperties.add(new EventProperty(XSD._long.toString(), "timestamp", ""));
		eventProperties.add(new EventProperty(XSD._string.toString(), "userName", ""));
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri("tcp://localhost:61616");
		grounding.setTopicName("SEPA.SEP.Twitter.Sample");
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Twitter Sample Stream");
		stream.setDescription("Twitter Sample Stream Description");
		stream.setUri(sep.getUri() + "/sample");
		System.out.println(stream.getUri());
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
			json.put("userName", status.getUser().getName());
			json.put("text", status.getText());
			json.put("name", "TwitterEvent");
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
			json.put("name", "TwitterGeoEvent");
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
