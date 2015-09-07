package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.google.gson.JsonObject;

import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class TaxiStreamGenerator implements Runnable {

	public static final String MEDALLION = "medallion";
	public static final String HACK_LICENSE = "hack_license";
	public static final String PICKUP_DATETIME = "pickup_datetime";
	public static final String DROPOFF_DATETIME = "dropoff_datetime";
	public static final String TRIP_TIME_IN_SECS = "trip_time_in_secs";
	public static final String TRIP_DISTANCE = "trip_distance";
	public static final String PICKUP_LONGITUDE = "pickup_longitude";
	public static final String PICKUP_LATITUDE= "pickup_latitude";
	public static final String DROPOFF_LONGITUDE = "dropoff_longitude";
	public static final String DROPOFF_LATITUDE = "dropoff_latitude";
	public static final String PAYMENT_TYPE = "payment_type";
	public static final String FARE_AMOUNT = "fare_amount";
	public static final String SURCHARGE = "surcharge";
	public static final String MTA_TAX = "mta_tax";
	public static final String TIP_AMOUNT = "tip_amount";
	public static final String TOLLS_AMOUNT = "tolls_amount";
	public static final String TOTAL_AMOUNT = "total_amount";
	public static final String READ_DATETIME = "read_datetime";
	
	private final File file;
	private final SimulationSettings settings;
	private IMessagePublisher publisher;
	private IMessagePublisher timePublisher;
	private boolean publishCurrentTime;
		
	public TaxiStreamGenerator(final File file, final SimulationSettings settings, IMessagePublisher publisher)
	{
		this.file = file;
		this.settings = settings;
		this.publisher = publisher;		
	}
	
	public TaxiStreamGenerator(final File file, final SimulationSettings settings, IMessagePublisher publisher, IMessagePublisher timePublisher)
	{
		this(file, settings, publisher);
		this.publishCurrentTime = true;
		this.timePublisher = timePublisher;
	}
	
	@Override
	public void run() {
		long previousDropoffTime = -1;
		System.out.println("Initializing Taxi Stream");
		
		Optional<BufferedReader> readerOpt = Utils.getReader(file);
		long start = System.currentTimeMillis();
		if (readerOpt.isPresent())
		{
			try {
				BufferedReader br = readerOpt.get();
				
				String line;
				long counter = 0;
				
				while ((line = br.readLine()) != null) {
					if (counter > -1)
					{
						try {
							counter++;
							String[] records = line.split(",");
							
							long currentDropOffTime = toTimestamp(records[3]);
	
							if (settings.isSimulateRealOccurrenceTime())
							{
								if (previousDropoffTime == -1) previousDropoffTime = currentDropOffTime;
								long diff = currentDropOffTime - previousDropoffTime;		
								if (diff > 0) 
									{
										System.out.println("Waiting " +diff/1000 + " seconds");
										Thread.sleep(diff/settings.getSpeedupFactor());
									}
								previousDropoffTime = currentDropOffTime;
							}				
							String json = buildJsonString(records);
							publisher.onEvent(json);
							if (publishCurrentTime) timePublisher.onEvent(String.valueOf(currentDropOffTime));
							if (counter % 10000 == 0) System.out.println(counter +" Events sent.");
						} catch (Exception e) { e.printStackTrace(); }
					}

				}
				br.close();		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		long end = System.currentTimeMillis();
		System.out.println("Took: " +(end-start));
	}

	private long toTimestamp(String formattedDate) 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = sdf.parse(formattedDate);
			return date.getTime();
		} catch (ParseException e) {
			return 0;
		}
		
	}
	
	private double toDouble(String field)
	{
		return Double.parseDouble(field);
	}
	
	public JsonObject buildJson(String[] line)
	{
		JsonObject json = new JsonObject();
		
		try {
			json.addProperty(MEDALLION, line[0]);
			json.addProperty(HACK_LICENSE, line[1]);
			json.addProperty("pickup_datetime", toTimestamp(line[2]));
			json.addProperty("dropoff_datetime", toTimestamp(line[3]));
			json.addProperty("trip_time_in_secs", Integer.parseInt(line[4]));
			json.addProperty("trip_distance", toDouble(line[5]));
			json.addProperty("pickup_longitude", toDouble(line[6]));
			json.addProperty("pickup_latitude", toDouble(line[7]));
			json.addProperty("dropoff_longitude", toDouble(line[8]));
			json.addProperty("dropoff_latitude", toDouble(line[9]));
			json.addProperty("payment_type", line[10]);
			json.addProperty("fare_amount", toDouble(line[11]));
			json.addProperty("surcharge", toDouble(line[12]));
			json.addProperty("mta_tax", toDouble(line[13]));
			json.addProperty("tip_amount", toDouble(line[14]));
			json.addProperty("tolls_amount", toDouble(line[15]));
			json.addProperty("total_amount", toDouble(line[16]));

			json.addProperty("read_datetime", System.currentTimeMillis());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public String buildJsonString(String[] line)
	{
		StringBuilder json = new StringBuilder();
		json.append("{");
		//new RdfProducer(line[0]).toRdf();
			json.append(Utils.toJsonString(MEDALLION, line[0]));
			json.append(Utils.toJsonString(HACK_LICENSE, line[1]));
			json.append(Utils.toJsonNumber(PICKUP_DATETIME, toTimestamp(line[2])));
			json.append(Utils.toJsonNumber(DROPOFF_DATETIME, toTimestamp(line[3])));
			json.append(Utils.toJsonNumber(TRIP_TIME_IN_SECS, Integer.parseInt(line[4])));
			json.append(Utils.toJsonNumber(TRIP_DISTANCE, toDouble(line[5])));
			json.append(Utils.toJsonNumber(PICKUP_LONGITUDE, toDouble(line[6])));
			json.append(Utils.toJsonNumber(PICKUP_LATITUDE, toDouble(line[7])));
			json.append(Utils.toJsonNumber(DROPOFF_LONGITUDE, toDouble(line[8])));
			json.append(Utils.toJsonNumber(DROPOFF_LATITUDE, toDouble(line[9])));
			json.append(Utils.toJsonString(PAYMENT_TYPE, line[10]));
			json.append(Utils.toJsonNumber(FARE_AMOUNT, toDouble(line[11])));
			json.append(Utils.toJsonNumber(SURCHARGE, toDouble(line[12])));
			json.append(Utils.toJsonNumber(MTA_TAX, toDouble(line[13])));
			json.append(Utils.toJsonNumber(TIP_AMOUNT, toDouble(line[14])));
			json.append(Utils.toJsonNumber(TOLLS_AMOUNT, toDouble(line[15])));
			json.append(Utils.toJsonNumber(TOTAL_AMOUNT, toDouble(line[16])));

			json.append(Utils.toJsonstr(READ_DATETIME, System.currentTimeMillis(), false));
			json.append("}");		
		return json.toString();
	}
}
