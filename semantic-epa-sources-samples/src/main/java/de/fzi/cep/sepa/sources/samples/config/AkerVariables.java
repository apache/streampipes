package de.fzi.cep.sepa.sources.samples.config;

public enum AkerVariables {

	DrillingRPM(1000693, "Drilling RPM", "Denotes the rotation speed of the DDM's main shaft with revolutions per miniute as Unit of Measurement"),
	DrillingTorque(1000700, "Drilling Torque", "Denotes the torque readout of the DDM's main shaft with kilo Newton meters as Unit of Measurement"),
	HookLoad(1002311, "Hook Load", "Denotes the weight carried by the Derrick Drilling Machine"),
	GearLubeOilTemperature(1000695, "Gear lube oil temperature", "Denotes the lube oil temperature in degrees Celsius present in the DDM gear box"),
	GearBoxPressure(1000692, "Gear Box Pressure", "Represents the lube oil pressure of the DDM's gear box"),
	SwivelOilTemperature(1000696, "Swivel Oil Temperature", "Denotes the lube oil pressure in degress celsius present in the main swivel bearing oil bath"),
	WeightOnBit(1002123, "Weight on Bit", "Denotes the load in Tons put on the drill bit during drilling"),
	OutdoorTemperature(1033619, "OutdoorTemperature", "Derived temperature of the surrounding at the rig with degree Celsius as Unit of Measurement"),
	RamPositionSetPoint(1002113, "Ram Position Set Point", "Denotes the calculated position from a machine's control system"),
	RamPositionMeasuredValue(1002115, "Ram Position Measured Value", "Denotes the Actual Position of the machine"),
	RamVelocitySetPoint(1002114, "Ram Velocity Set Point", "Denotes the calculated velocity from a machine's control system"),
	RamVelocityMeasuredValue(1002116, "Ram Velocity Measured Value", "Denotes the actual speed of machine movement"),
	MRUPosition(1002127, "Motion Reference Unit Position", "denotes the relative, vertical position of the rig due to wave motions"),
	MRUVelocity(1002128, "MRU velocity", "denotes the rig's vertical velocity with mm/s as Unit of Measurement");
	
	long tagNumber;
	String eventName;
	String description;
	
	AkerVariables(long tagNumber, String eventName, String description)
	{
		this.tagNumber = tagNumber;
		this.eventName = eventName;
		this.description = description;
	}
	
	public long tagNumber()
	{
		return tagNumber;
	}
	
	public String eventName()
	{
		return eventName;
	}
	
	public String description()
	{
		return description;
	}
}
