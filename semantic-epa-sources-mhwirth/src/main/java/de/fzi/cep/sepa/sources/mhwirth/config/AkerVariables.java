package de.fzi.cep.sepa.sources.mhwirth.config;

public enum AkerVariables {

    DrillingRPM(1000693, "Drilling RPM", "Denotes the rotation speed of the DDM's main shaft with revolutions per miniute as Unit of Measurement","SEPA.SEP.DDM.SpeedShaft"),
	DrillingTorque(1000700, "Drilling Torque", "Denotes the torque readout of the DDM's main shaft with kilo Newton meters as Unit of Measurement", "SEPA.SEP.DDM.Torque"),
	HookLoad(1002311, "Hook Load", "Denotes the weight carried by the Derrick Drilling Machine", "SEPA.SEP.DDM.HookLoad"),
	GearLubeOilTemperature(1000695, "Gear lube oil temperature", "Denotes the lube oil temperature in degrees Celsius present in the DDM gear box", "SEPA.SEP.DDM.GearboxOilTemperature"),
	GearBoxPressure(1000692, "Gear Box Pressure", "Represents the lube oil pressure of the DDM's gear box", "SEPA.SEP.DDM.GearBoxPressure"),
	SwivelOilTemperature(1000696, "Swivel Oil Temperature", "Denotes the lube oil pressure in degress celsius present in the main swivel bearing oil bath", "SEPA.SEP.DDM.SwivelTemperature"),
	WeightOnBit(1002123, "Weight on Bit", "Denotes the load in Tons put on the drill bit during drilling", "SEPA.SEP.DrillBit.WeightOnBit"),
	OutdoorTemperature(1033619, "OutdoorTemperature", "Derived temperature of the surrounding at the rig with degree Celsius as Unit of Measurement", "SEPA.SEP.Weather.Temperature"),
	RamPositionSetPoint(1002113, "Ram Position Set Point", "Denotes the calculated position from a machine's control system", "SEPA.SEP.Ram.PositionSetPoint"),
	RamPositionMeasuredValue(1002115, "Ram Position Measured Value", "Denotes the Actual Position of the machine", "SEPA.SEP.Ram.PositionMeasuredValue"),
	RamVelocitySetPoint(1002114, "Ram Velocity Set Point", "Denotes the calculated velocity from a machine's control system", "SEPA.SEP.Ram.VelocitySetPoint"),
	RamVelocityMeasuredValue(1002116, "Ram Velocity Measured Value", "Denotes the actual speed of machine movement", "SEPA.SEP.Ram.VelocityMeasuredValue"),
	MRUPosition(1002127, "Motion Reference Unit Position", "denotes the relative, vertical position of the rig due to wave motions", "SEPA.SEP.MRU.Position"),
	MRUVelocity(1002128, "MRU velocity", "denotes the rig's vertical velocity with mm/s as Unit of Measurement", "SEPA.SEP.MRU.Velocity"),
	Ibop(10001, "Ibop status", "", "SEPA.SEP.DDM.Ibop"),
    Enriched(10000, "Enriched stream", "", "SEPA.SEP.Enriched"),
	Friction_Swivel(10002, "Friction Coefficient (Swivel)", "A data stream that provides current friction coefficient values of the swivel.", "eu.proasense.streamstory.output.coefficient.swivel"),
	Friction_Gearbox(10003, "Friction Coefficient (Gearbox)", "A data stream that provides current friction coefficient values of the gearbox.", "eu.proasense.streamstory.output.coefficient.gearbox");



	long tagNumber;
	String eventName;
	String description;
	String topic;
	
	AkerVariables(long tagNumber, String eventName, String description, String topic)
	{
		this.tagNumber = tagNumber;
		this.eventName = eventName;
		this.description = description;
		this.topic = topic;
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

	public String originalTopic() {
		return topic;
	}
	
	public String topic()
	{
		String topic = "eu.proasense.internal.sp.internal.outgoing." +tagNumber;
		return topic;
	}
}
