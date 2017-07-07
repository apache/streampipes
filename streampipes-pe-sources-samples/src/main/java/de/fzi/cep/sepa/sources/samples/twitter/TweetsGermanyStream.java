package de.fzi.cep.sepa.sources.samples.twitter;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.jms.ActiveMQPublisher;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import org.codehaus.jettison.json.JSONObject;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

public class TweetsGermanyStream implements EventStreamDeclarer{

	ActiveMQPublisher publisher;
	
	public TweetsGermanyStream() throws JMSException {
		publisher = new ActiveMQPublisher(ClientConfiguration.INSTANCE.getJmsHost() +":61616", "SEPA.SEP.Twitter.Germany");
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "text", "", de.fzi.cep.sepa.commons.Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "latitude", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "longitude", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "userName", "", de.fzi.cep.sepa.commons.Utils.createURI("http://foaf/name")));
		
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Twitter.Germany"));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("Twitter Sample Stream (DE)");
		stream.setDescription("Tweets written in Germany");
		stream.setUri(sep.getUri() + "/de");
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
		
		double[][] location = {{5.998535, 45.958310}, {16.303711, 55.390812}};
	
		 StatusListener listener = new StatusListener(){
		        public void onStatus(Status status) {
		        	try {
						publisher.sendText(buildJson(status).toString());
					} catch (JMSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
		    FilterQuery query = new FilterQuery();
		    query.locations(location);
		    twitterStream.filter(query);
		
	}
	
	private JSONObject buildJson(Status status)
	{
		JSONObject json = new JSONObject();
		
		try {
			json.put("latitude", status.getGeoLocation().getLatitude());
			json.put("longitude", status.getGeoLocation().getLongitude());
			json.put("timestamp", status.getCreatedAt().getTime());
			json.put("userName", status.getUser().getName());
			json.put("text", status.getText());
			json.put("name", "TwitterGermanyEvent");
		} catch (Exception e) {
		}
		
		return json;
	}
	

	@Override
	public boolean isExecutable() {
		return false;
	}
}

