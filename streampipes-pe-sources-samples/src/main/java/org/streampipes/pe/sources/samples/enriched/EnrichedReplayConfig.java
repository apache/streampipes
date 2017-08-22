package org.streampipes.pe.sources.samples.enriched;

import java.io.File;

public class EnrichedReplayConfig {

	public static final String dataDirectory = System.getProperty("user.home") + File.separator +".streampipes" +File.separator +"sources" +File.separator +"data" +File.separator;
	
	public static final String filenamePrefix = "drilling-2015-";
	
	public static final int firstFileId = 1;
	
	public static final int lastFileId = 10;
	
	public static final String TIME = "time";
	public static final String HOIST_PRESS_A = "hoist_press_A";
	public static final String HOIST_PRESS_B = "hoist_press_B";
	public static final String HOOK_LOAD = "hook_load";
	public static final String IBOP = "ibop";
	public static final String OIL_TEMP_GEARBOX = "oil_temp_gearbox";
	public static final String OIL_TEMP_SWIVEL = "oil_temp_swivel";
	public static final String PRESSURE_GEARBOX = "pressure_gearbox";
	public static final String RPM = "rpm";
	public static final String TEMP_AMBIENT = "temp_ambient";
	public static final String TORQUE = "torque";
	public static final String WOB = "wob";
	public static final String MRU_POS = "mru_pos";
	public static final String MRU_VEL = "mru_vel";
	public static final String RAM_POS_MEASURED = "ram_pos_measured";
	public static final String RAM_POS_SETPOINT = "ram_pos_setpoint";
	public static final String RAM_VEL_MEASURED = "ram_vel_measured";
	public static final String RAM_VEL_SETPOINT = "ram_vel_setpoint";
	
}
