package de.fzi.cep.sepa.sources.samples.taxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.codehaus.jettison.json.JSONObject;

import de.fzi.cep.sepa.sources.samples.activemq.IMessagePublisher;

public class TaxiStreamGenerator implements Runnable{

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
		
		Optional<BufferedReader> readerOpt = getReader(file);
		
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
							
							JSONObject obj = buildJson(records);
							if (obj.getDouble("pickup_latitude") > 0) publisher.onEvent(obj.toString());
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
	}
	
	private Optional<BufferedReader> getReader(File file2) {
		try {
			return Optional.of(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Optional.empty();
		}
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

			json.put("read_datetime", System.currentTimeMillis());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	

}
