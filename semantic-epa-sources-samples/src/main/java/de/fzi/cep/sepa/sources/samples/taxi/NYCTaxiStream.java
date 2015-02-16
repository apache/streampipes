package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.ontoware.rdf2go.vocabulary.XSD;

import twitter4j.Status;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commonss.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class NYCTaxiStream implements EventStreamDeclarer {

	ActiveMQPublisher publisher;
	
	public static final Logger logger = Logger.getLogger(NYCTaxiStream.class);
	
	public static final String FILENAME = "C:\\Users\\riemer\\Downloads\\sorted_data.csv\\sorted_data.csv";
	
	public NYCTaxiStream() throws JMSException {
		publisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "SEPA.SEP.NYC.Taxi");
	}
	
	@Override
	public EventStream declareModel(SEP sep) {
		
		EventStream stream = new EventStream();
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventProperty(XSD._string.toString(), "medallion", "", Utils.createURI("http://test.de/text")));
		eventProperties.add(new EventProperty(XSD._string.toString(), "hack_license", "", Utils.createURI("http://test.de/text")));
		eventProperties.add(new EventProperty(XSD._long.toString(), "pickup_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventProperty(XSD._long.toString(), "dropoff_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventProperty(XSD._integer.toString(), "trip_time_in_secs", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "trip_distance", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "pickup_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "pickup_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "dropoff_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "dropoff_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventProperty(XSD._string.toString(), "payment_type", "", Utils.createURI("http://test.de/text")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "fare_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "surcharge", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "mta_tax", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "tip_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "tolls_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventProperty(XSD._double.toString(), "total_amount", "", Utils.createURI("http://schema.org/Number")));
		
		EventGrounding grounding = new EventGrounding();
		grounding.setPort(61616);
		grounding.setUri(Configuration.TCP_SERVER_URL);
		grounding.setTopicName("SEPA.SEP.NYC.Taxi");
		
		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName("NYC Taxi Sample Stream");
		stream.setDescription("NYC Taxi Sample Stream Description");
		stream.setUri(sep.getUri() + "/sample");
		System.out.println(stream.getUri());
	
		return stream;
	}

	@Override
	public void executeStream() {
		
		Runnable r = new Runnable() {
			
		@Override
		public void run() {
		
				File file = new File(FILENAME);
				long previousDropoffTime = -1;
				
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(file));
					
					String line;
					
					while ((line = br.readLine()) != null) {
						String[] records = line.split(",");
						long currentDropOffTime = toTimestamp(records[3]);
						
						if (previousDropoffTime == -1) previousDropoffTime = currentDropOffTime;
						
						long diff = currentDropOffTime - previousDropoffTime;
						logger.info("Waiting " +diff/1000 + " seconds");
						if (diff > 0) Thread.sleep(diff);
						previousDropoffTime = currentDropOffTime;
						
						String json = buildJson(records).toString();
						publisher.sendText(json);
						logger.info(json);
					
					}
					
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	private long toTimestamp(String formattedDate) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(formattedDate);
		return date.getTime();
	}
	
	public JSONObject buildJson(String[] line)
	{
		JSONObject json = new JSONObject();
		
		try {
			json.put("medallion", line[0]);
			json.put("hack_license", line[1]);
			json.put("pickup_datetime", toTimestamp(line[2]));
			json.put("dropoff_datetime", toTimestamp(line[3]));
			json.put("trip_time_in_secs", line[4]);
			json.put("trip_distance", line[5]);
			json.put("pickup_longitude", line[6]);
			json.put("pickup_latitude", line[7]);
			json.put("dropoff_longitude", line[8]);
			json.put("dropoff_latitude", line[9]);
			json.put("payment_type", line[10]);
			json.put("fare_amount", line[11]);
			json.put("surcharge", line[12]);
			json.put("mta_tax", line[13]);
			json.put("tip_amount", line[14]);
			json.put("tolls_amount", line[15]);
			json.put("total_amount", line[16]);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}

}
