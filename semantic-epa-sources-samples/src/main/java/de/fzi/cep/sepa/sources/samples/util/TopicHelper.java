package de.fzi.cep.sepa.sources.samples.util;

import de.fzi.cep.sepa.sources.samples.config.AkerVariables;

public class TopicHelper {

	public TopicHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTopicByTagNumber(int tagNumber)
	{
		if (tagNumber == 1000693) return AkerVariables.DrillingRPM.topic();
		else if (tagNumber == 1000700) return AkerVariables.DrillingTorque.topic();
		else if (tagNumber == 1002311) return AkerVariables.HookLoad.topic();
		else if (tagNumber == 1000695) return AkerVariables.GearLubeOilTemperature.topic();
		else return AkerVariables.GearBoxPressure.topic();
	}

}
