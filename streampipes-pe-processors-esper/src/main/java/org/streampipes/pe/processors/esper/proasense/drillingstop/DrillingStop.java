package org.streampipes.pe.processors.esper.proasense.drillingstop;

import org.streampipes.wrapper.esper.EsperEventEngine;

import java.util.ArrayList;
import java.util.List;

public class DrillingStop extends EsperEventEngine<DrillingStopParameters>{

	@Override
	protected List<String> statements(DrillingStopParameters bindingParameters) {
		/*
		 * select * from pattern[every RPM(rpm > threshold) -> Torque(torque > threshold) where timer:within(10 secs)
		 */
		bindingParameters.getOutputProperties().forEach(property -> System.out.println(property));
		List<String> statements = new ArrayList<>();
		String rpmInName = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String torqueInName = fixEventName(bindingParameters.getInputStreamParams().get(1).getInName());
		String pattern = "select '1' as drilingStatus, s2.variable_type as variable_type, s2.variable_timestamp as variable_timestamp, s2.value as value, s4.variable_type as variable_type1, s4.variable_timestamp as variable_timestamp1, s4.value as value1 from pattern[every ((s1=" +rpmInName 
				+"(" +bindingParameters.getRpmPropertyName() 
				+">" +bindingParameters.getMinRpm() +") -> s2=" 
				+rpmInName +"(" 
				+bindingParameters.getRpmPropertyName() +"<=" 
				+bindingParameters.getMinRpm() +"))"
				+" and "
				+"(s3=" +torqueInName
				+"(s3." +bindingParameters.getTorquePropertyName() 
				+">" +bindingParameters.getMinTorque() +") -> s4=" 
				+rpmInName +"(s4." 
				+bindingParameters.getTorquePropertyName() +"<=" 
				+bindingParameters.getMinTorque() +"))"
						+ ")]";
		
		System.out.println(pattern);
		statements.add(pattern);
		return statements;
	}

}
