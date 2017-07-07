package de.fzi.cep.sepa.sources.samples.wunderbar;

public enum WunderbarVariables {

	ACCELEROMETER("Accelerometer 1", "Measures proper acceleration (g-force).", "wunderbar.F2:28:18:23:51:29.acceleration", "/accelerometer"),
	ANGULAR_SPEED("Angular Speed 1", "Measures the angular rate of movement, i.e. the speed of the change in the angle of an object across all axes.", "wunderbar.F2:28:18:23:51:29.angularSpeed", "/angularspeed"),
	NOISE_LEVEL("Noise Level 1", "This sensor measures the average ambient noise level. ", "wunderbar.C4:89:87:4F:58:08.noiseLevel", "/noiselevel"),
	TEMPERATURE("Temperature 1", "Measures the current room temperature.", "wunderbar.E7:2C:BE:32:2A:DD.temperature", "/temperature"),
	COLOR("Color 1", "Detects the amount of light reflected off an object and integrates the signal into a digital value (rgb).", "wunderbar.C7:D7:45:4B:25:9B.color", "/color"),
	PROXIMITY("Proximity 1", "For proximity detection an external InfraRed LED is used to emit light, which is then measured by the integrated light detector to determine the amount of reflected light from the object in the light path.", "wunderbar.C7:D7:45:4B:25:9B.proximity", "/proximity"),
	LUMINOSITY("Luminosity 1", "detect the amount of light reflected off the object and integrate the signal into a digital value (white).", "wunderbar.C7:D7:45:4B:25:9B.luminosity", "/luminosity"),
	HUMIDITY("Humidity 1", "The sensor measures relative humidity (RH), which is the ratio of the pressure of water vapor to pressure of water saturation vapor in the same temperature. ", "wunderbar.E7:2C:BE:32:2A:DD.humidity", "/humidity"),
	
	ACCELEROMETER_2("Accelerometer 2", "Measures proper acceleration (g-force).", "wunderbar.FB:AE:36:3E:8E:29.acceleration", "/accelerometer"),
	ANGULAR_SPEED_2("Angular Speed 2", "Measures the angular rate of movement, i.e. the speed of the change in the angle of an object across all axes.", "wunderbar.FB:AE:36:3E:8E:29.angularSpeed", "/angularspeed"),
	NOISE_LEVEL_2("Noise Level 2", "This sensor measures the average ambient noise level. ", "wunderbar.C0:9F:3C:5F:E1:21.noiseLevel", "/noiselevel"),
	TEMPERATURE_2("Temperature 2", "Measures the current room temperature.", "wunderbar.DC:0E:7C:9F:42:16.temperature", "/temperature"),
	COLOR_2("Color 2", "Detects the amount of light reflected off an object and integrates the signal into a digital value (rgb).", "wunderbar.EA:2E:AE:39:F9:21.color", "/color"),
	PROXIMITY_2("Proximity 2", "For proximity detection an external InfraRed LED is used to emit light, which is then measured by the integrated light detector to determine the amount of reflected light from the object in the light path.", "wunderbar.EA:2E:AE:39:F9:21.proximity", "/proximity"),
	LUMINOSITY_2("Luminosity 2", "detect the amount of light reflected off the object and integrate the signal into a digital value (white).", "wunderbar.EA:2E:AE:39:F9:21.luminosity", "/luminosity"),
	HUMIDITY_2("Humidity 2", "The sensor measures relative humidity (RH), which is the ratio of the pressure of water vapor to pressure of water saturation vapor in the same temperature. ", "wunderbar.DC:0E:7C:9F:42:16.humidity", "/humidity");
	
	
	String eventName;
	String description;
	String topic;
	String path;
	
	
	WunderbarVariables(String eventName, String description, String topic, String path)
	{
		this.eventName = eventName;
		this.description = description;
		this.topic = topic;
		this.path = path;
	}
	
	
	public String eventName()
	{
		return eventName;
	}
	
	public String description()
	{
		return description;
	}
	
	public String path() {
		return path;
	}
	
	public String topic()
	{
		return topic;
	}
}
