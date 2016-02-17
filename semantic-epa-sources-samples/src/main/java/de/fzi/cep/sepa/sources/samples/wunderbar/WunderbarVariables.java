package de.fzi.cep.sepa.sources.samples.wunderbar;

public enum WunderbarVariables {

	ACCELEROMETER("Accelerometer", "Measures proper acceleration (g-force).", "wunderbar.F2:28:18:23:51:29.acceleration", "/accelerometer"),
	ANGULAR_SPEED("Angular Speed", "Measures the angular rate of movement, i.e. the speed of the change in the angle of an object across all axes.", "wunderbar.F2:28:18:23:51:29.angularSpeed", "/angularspeed"),
	NOISE_LEVEL("Noise Level", "This sensor measures the average ambient noise level. ", "wunderbar.C4:89:87:4F:58:08.noiseLevel", "/noiselevel"),
	TEMPERATURE("Temperature", "Measures the current room temperature.", "wunderbar.E7:2C:BE:32:2A:DD.temperature", "/temperature"),
	COLOR("Color", "Detects the amount of light reflected off an object and integrates the signal into a digital value (rgb).", "wunderbar.C7:D7:45:4B:25:9B.color", "/color"),
	PROXIMITY("Proximity", "For proximity detection an external InfraRed LED is used to emit light, which is then measured by the integrated light detector to determine the amount of reflected light from the object in the light path.", "wunderbar.C7:D7:45:4B:25:9B.proximity", "/proximity"),
	LUMINOSITY("Luminosity", "detect the amount of light reflected off the object and integrate the signal into a digital value (white).", "wunderbar.C7:D7:45:4B:25:9B.luminosity", "/luminosity"),
	HUMIDITY("Humidity", "The sensor measures relative humidity (RH), which is the ratio of the pressure of water vapor to pressure of water saturation vapor in the same temperature. ", "wunderbar.E7:2C:BE:32:2A:DD.humidity", "/humidity");
	
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
