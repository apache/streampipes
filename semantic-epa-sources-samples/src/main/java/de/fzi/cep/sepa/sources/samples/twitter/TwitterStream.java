package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEP;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStream implements EventStreamDeclarer {
	
	
	@Override
	public EventStream declareStream(SEP sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventProperty(XSD._string.toString(), "text", ""));
		eventProperties.add(new EventProperty(XSD._long.toString(), "timestamp", ""));
		eventProperties.add(new EventProperty(XSD._double.toString(), "latitude", ""));
		eventProperties.add(new EventProperty(XSD._double.toString(), "longitude", ""));
		eventProperties.add(new EventProperty(XSD._string.toString(), "userName", ""));
		
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		//stream.setName(name);
		
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
			 int i = 0;
		        public void onStatus(Status status) {
		            System.out.println(i++);
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
					// TODO Auto-generated method stub
					
				}
		    };
		    
		    
		    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		 
		    twitterStream.addListener(listener);
		    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
		    twitterStream.sample();
		
	}

}
