package org.streampipes.pe.processors.esper.debs.c1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EventSender;
import com.espertech.esper.client.time.CurrentTimeEvent;

import org.streampipes.wrapper.esper.EsperEngineSettings;

public class TaxiDataInputProvider implements Runnable {
	
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
	
	public static final String COUNT_VALUE = "countValue";
	public static final String LIST = "list";
	public static final String BEST_CELLS = "bestCells";
	public static final String CELLOPTIONS = "cellOptions";
	public static final String CELLOPTIONS_1 = "cellOptions1";
	public static final String CELLX = "cellX";
	public static final String CELLY = "cellY";
	public static final String DOT = ".";
	public static final String EMPTY_TAXIS = "emptyTaxis";
	public static final String MEDIAN_PROFIT = "medianFare";
	public static final String PROFITABILITY = "profitability";

	//public static String FILENAME = "C:\\Users\\riemer\\Downloads\\sorted_data.csv\\sorted_data.csv";
	public static String FILENAME = "C:\\Users\\riemer\\Downloads\\debs-dc\\test01\\test_01.csv";
	
	//Complete dataset
	//public static final String FILENAME = "C:\\Users\\riemer\\Downloads\\sorted_data_complete.csv\\sorted_data.csv";
	
	private String eventName;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TaxiDataInputProvider(String eventName)
	{
		this.eventName = eventName;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Start sending Events");
		com.espertech.esper.client.EPRuntime runtime = EsperEngineSettings.epService.getEPRuntime();
		EventSender sender = runtime.getEventSender(eventName);
		File file = new File(FILENAME);
		
		long previousDropoffTime = -1;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			
			String line;
			long counter = 0;
			List<String> records = new ArrayList<String>();
			runtime.sendEvent(new StatusEvent(true, System.nanoTime(), 0));
			while ((line = br.readLine()) != null) 
			{
				counter++;
				records.clear();
	            int pos = 0, end;
	            
	            while ((end = line.indexOf(',', pos)) >= 0) 
	            {
	                records.add(line.substring(pos, end));
	                pos = end + 1;
	            }
	            
	            records.add(line.substring(line.lastIndexOf(',')+1, line.length()));
				
				try 
				{
					Map<String, Object> obj = buildMap(records);
					
					long currentDropOffTime = (long) obj.get(DROPOFF_DATETIME);
					
					
					if (((double)obj.get(PICKUP_LATITUDE)) != 0.0) 
					{
						if (((double)obj.get(DROPOFF_LATITUDE)) != 0.0) 
						{
							//System.out.println(obj.toString());
							sender.sendEvent(obj);
						}
					}
					if (currentDropOffTime > previousDropoffTime)
					{
						CurrentTimeEvent timeEvent = new CurrentTimeEvent(currentDropOffTime);
						runtime.sendEvent(timeEvent);
						previousDropoffTime = currentDropOffTime;
					}
					
				} catch (Exception e)
				{
					System.err.println("Could not parse line");
				}
				if (counter % 100000 == 0) System.out.println(counter +" Events sent.");
			}
			System.out.println("Finished");
			runtime.sendEvent(new StatusEvent(false, System.nanoTime(), counter));
			runtime.sendEvent(new CurrentTimeEvent(System.currentTimeMillis()));
			br.close();
		
		} catch (EPException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
	} 
	}
	
	public Map<String, Object> buildMap(List<String> line) throws Exception
	{
		Map<String, Object> json = new HashMap<>();
		
		json.put(MEDALLION, line.get(0));
		json.put(HACK_LICENSE, line.get(1));
		json.put(PICKUP_DATETIME, toTimestamp(line.get(2)));
		json.put(DROPOFF_DATETIME, toTimestamp(line.get(3)));
		json.put(TRIP_TIME_IN_SECS, Integer.parseInt(line.get(4)));
		json.put(TRIP_DISTANCE, Double.parseDouble(line.get(5)));
		json.put(PICKUP_LONGITUDE, Double.parseDouble(line.get(6)));	
		json.put(PICKUP_LATITUDE, Double.parseDouble(line.get(7)));
		json.put(DROPOFF_LONGITUDE, Double.parseDouble(line.get(8)));
		json.put(DROPOFF_LATITUDE, Double.parseDouble(line.get(9)));
		json.put(PAYMENT_TYPE, line.get(10));
		json.put(FARE_AMOUNT, Double.parseDouble(line.get(11)));
		json.put(SURCHARGE, Double.parseDouble(line.get(12)));
		json.put(MTA_TAX, Double.parseDouble(line.get(13)));
		json.put(TIP_AMOUNT, Double.parseDouble(line.get(14)));
		json.put(TOLLS_AMOUNT, Double.parseDouble(line.get(15)));
		json.put(TOTAL_AMOUNT, Double.parseDouble(line.get(16)));

		//for later delay calculation
		json.put(READ_DATETIME, System.nanoTime());
		
		return json;
	}
	

	private long toTimestamp(String formattedDate) throws ParseException
	{
		Date date = sdf.parse(formattedDate);
		return date.getTime();
	}
	
	public static void main(String[] args)
	{
		new Thread(new TaxiDataInputProvider("taxi")).start();
	}

}
