package de.fzi.cep.sepa.esper.drillingstart.single;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EventSender;
import com.espertech.esper.client.time.CurrentTimeEvent;

import de.fzi.cep.sepa.esper.main.EsperEngineSettings;

public class EnrichedDataSimulator implements Runnable {
	
	private static final String FILENAME = "g:\\AkerData\\drilling-2015-001.csv";
	private String eventName = "";
	private long currentTime = 0;
	
	private static final String TIME = "time";
	private static final String HOIST_PRESS_A = "hoist_press_A";
	private static final String HOIST_PRESS_B = "hoist_press_B";
	private static final String HOOK_LOAD = "HOOK_LOAD";
	private static final String IBOP = "IBOP";
	private static final String OIL_TEMP_GEARBOX = "oil_temp_gearbox";
	private static final String OIL_TEMP_SWIVEL = "oil_temp_swivel";
	private static final String PRESSURE_GEARBOX = "PRESSURE_GEARBOX";
	private static final String RPM = "rpm";
	private static final String TEMP_AMBIENT = "temp_ambient";
	private static final String TORQUE = "torque";
	private static final String WOB = "wob";
	private static final String MRU_POS = "mru_pos";
	private static final String MRU_VEL = "mru_vel";
	private static final String RAM_POS_MEASURED = "ram_pos_measured";
	private static final String RAM_POS_SETPOINT = "ram_pos_setpoint";
	private static final String RAM_VEL_MEASURED = "ram_vel_measured";
	private static final String RAM_VEL_SETPOINT = "ram_vel_setpoint";
	
	public EnrichedDataSimulator(String inName) {
		this.eventName = inName;
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

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			
			String line;
			long counter = 0;
			List<String> records = new ArrayList<String>();
			while ((line = br.readLine()) != null) 
			{
				counter++;
				if (counter > -1)
				{
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
						long nextTime = ((long) obj.get("time") / 1000);
						if (nextTime > currentTime)
						{
							runtime.sendEvent(new CurrentTimeEvent(nextTime));
							currentTime = nextTime;
						}
						sender.sendEvent(obj);
						
					} catch (Exception e)
					{
						System.err.println("Could not parse line");
					}
				}
				if (counter % 100000 == 0) System.out.println(counter +" Events sent.");
			}
			System.out.println("Finished");
			runtime.sendEvent(new CurrentTimeEvent(System.currentTimeMillis()));
			br.close();
		
		} catch (EPException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
	} 
	}

	private Map<String, Object> buildMap(List<String> records) {
		Map<String, Object> map = new HashMap<>();
		
		map.put(TIME, Long.parseLong(records.get(0)));
		map.put(HOIST_PRESS_A, Double.parseDouble(records.get(1)));
		map.put(HOIST_PRESS_B, Double.parseDouble(records.get(2)));
		map.put(HOOK_LOAD, Double.parseDouble(records.get(3)));
		map.put(IBOP, Integer.parseInt(records.get(4)));
		map.put(OIL_TEMP_GEARBOX, Double.parseDouble(records.get(5)));
		map.put(OIL_TEMP_SWIVEL, Double.parseDouble(records.get(6)));
		map.put(PRESSURE_GEARBOX, Double.parseDouble(records.get(7)));
		map.put(RPM, Double.parseDouble(records.get(8)));
		map.put(TEMP_AMBIENT, Double.parseDouble(records.get(9)));
		map.put(TORQUE, Double.parseDouble(records.get(10)));
		map.put(WOB, Double.parseDouble(records.get(11)));
		map.put(MRU_POS, Double.parseDouble(records.get(12)));
		map.put(MRU_VEL, Double.parseDouble(records.get(13)));
		map.put(RAM_POS_MEASURED, Double.parseDouble(records.get(14)));
		map.put(RAM_POS_SETPOINT, Double.parseDouble(records.get(15)));
		map.put(RAM_VEL_MEASURED, Double.parseDouble(records.get(16)));
		map.put(RAM_VEL_SETPOINT, Double.parseDouble(records.get(17)));
		return map;
	}

}
