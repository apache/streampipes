package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import twitter4j.Status;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class NYCTaxiStream implements EventStreamDeclarer {

	ActiveMQPublisher publisher;
	ActiveMQPublisher timePublisher;
	
	public static final Logger logger = Logger.getLogger(NYCTaxiStream.class);
	
	public static final String FILENAME = "C:\\Users\\riemer\\Downloads\\sorted_data.csv\\sorted_data.csv";
	
	//public static String FILENAME = "/home/fzi/Downloads/sorted_data.csv";
	//public static final String FILENAME = "/home/robin/FZI/CEP/sorted_data.csv";

	public NYCTaxiStream() throws JMSException {
		publisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "SEPA.SEP.NYC.Taxi");
		timePublisher = new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":61616", "FZI.Timer");
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
			
		EventStream stream = new EventStream();
		stream.setIconUrl(SourcesConfig.iconBaseUrl + "/Taxi_Icon_2" +"_HQ.png");
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "medallion", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "hack_license", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "pickup_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "dropoff_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "trip_time_in_secs", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "trip_distance", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "payment_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "fare_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "surcharge", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "mta_tax", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "tip_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "tolls_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "total_amount", "", Utils.createURI("http://schema.org/Number")));

		//current time for later delay calculation
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "read_datetime", "", Utils.createURI("http://schema.org/Number")));


		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.NYC.Taxi"));
		grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));
		
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
					long counter = 0;
					
					while ((line = br.readLine()) != null) {
						if (counter > -1)
						{
							counter++;
							String[] records = line.split(",");
							long currentDropOffTime = toTimestamp(records[3]);
							
							if (previousDropoffTime == -1) previousDropoffTime = currentDropOffTime;
							
							long diff = currentDropOffTime - previousDropoffTime;
							System.out.println("Waiting " +diff/1000 + " seconds");
							if (diff > 0) Thread.sleep(diff/10); //TODO change back to diff
							previousDropoffTime = currentDropOffTime;
							
							JSONObject obj = buildJson(records);
							if (obj.getDouble("pickup_latitude") > 0) publisher.sendText(obj.toString());
							//if (diff > 0) timePublisher.sendText(String.valueOf(currentDropOffTime));
							if (counter % 10000 == 0) System.out.println(counter +" Events sent.");
						}

					}
					br.close();
					System.out.println("finished!");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
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
			json.put("trip_time_in_secs", Integer.parseInt(line[4]));
			json.put("trip_distance", Double.parseDouble(line[5]));
			json.put("pickup_longitude", Double.parseDouble(line[6]));
			json.put("pickup_latitude", Double.parseDouble(line[7]));
			json.put("dropoff_longitude", Double.parseDouble(line[8]));
			json.put("dropoff_latitude", Double.parseDouble(line[9]));
			json.put("payment_type", line[10]);
			json.put("fare_amount", Double.parseDouble(line[11]));
			json.put("surcharge", Double.parseDouble(line[12]));
			json.put("mta_tax", Double.parseDouble(line[13]));
			json.put("tip_amount", Double.parseDouble(line[14]));
			json.put("tolls_amount", Double.parseDouble(line[15]));
			json.put("total_amount", Double.parseDouble(line[16]));

			//for later delay calculation
			Date date = new Date();
			json.put("read_datetime", System.currentTimeMillis());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return json;
	}


	/**
	 * Sending
	 */
	class OutputThread implements Runnable {
		long diff;

		public OutputThread(long sleepTime) {
			diff = sleepTime;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(diff);
				synchronized (publisher) {
					//publisher.sendText(json);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
