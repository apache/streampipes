package org.streampipes.pe.processors.esper.single;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.pe.processors.esper.EsperEventEngine;
import org.streampipes.model.impl.eventproperty.EventProperty;

public class DrillingStartEnriched extends EsperEventEngine<DrillingStartEnrichedParameters> {

	@Override
	protected List<String> statements(DrillingStartEnrichedParameters bindingParameters) {
		/*
		 * select * from pattern[every RPM(rpm > threshold) -> Torque(torque > threshold) where timer:within(10 secs)
		 */
		bindingParameters.getOutputProperties().forEach(property -> System.out.println(property));
		List<String> statements = new ArrayList<>();
		String eventInName = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String selectClause = makeSelectClause(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties());

		String pattern = "insert into DrillingStartDetection " +selectClause + " from pattern [ (every (s1=" +eventInName +" -> s2=" +eventInName +")) -> (timer:interval(2 sec) and not s3=" +eventInName +"(" +bindingParameters.getRpmPropertyName() +"<=5, " +bindingParameters.getTorquePropertyName() +"<=5" +")" +")] ";
		pattern += " where (s2." +bindingParameters.getTorquePropertyName() +" > 5 and s2." +bindingParameters.getRpmPropertyName() +" > 5) and ";
		pattern += "(s1." +bindingParameters.getTorquePropertyName() +" <= 5 or s1." +bindingParameters.getRpmPropertyName() +" <= 5) and ";
		pattern +="((s1." +bindingParameters.getTorquePropertyName() +" != s2." +bindingParameters.getTorquePropertyName() +") or (s1." +bindingParameters.getRpmPropertyName() +" != s2." +bindingParameters.getRpmPropertyName() +"))";
		
//		String selectClauseStop = makeSelectClauseDrillingStop(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties());
//		String pattern2 = selectClauseStop + " from pattern [every (s1=" +eventInName +" -> s2=" +eventInName +")] ";
//		pattern2 += " where (s2." +bindingParameters.getTorquePropertyName() +" < 5 and s2." +bindingParameters.getRpmPropertyName() +" < 5) and ";
//		pattern2 += "(s1." +bindingParameters.getTorquePropertyName() +" >= 5 or s1." +bindingParameters.getRpmPropertyName() +" >= 5) and ";
//		pattern2 +="((s1." +bindingParameters.getTorquePropertyName() +" != s2." +bindingParameters.getTorquePropertyName() +") or (s1." +bindingParameters.getRpmPropertyName() +" != s2." +bindingParameters.getRpmPropertyName() +"))";
//		
		
		String avgRpmTorquePattern = "insert into AvgRpmTorque select avg(rpm*torque) as averageTorque from " +eventInName  +".win:time(5 sec)";
		String lastAvgAfterDrillingStart = "insert into LastAvgRpmTorque select a3.averageTorque as averageTorque from pattern [every d1=DrillingStartDetection -> timer:interval(2 sec)] unidirectional, AvgRpmTorque.std:lastevent() as a3";
		
		String selectClauseStop = makeSelectClauseDrillingStop(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties());
		String patternDrillingStop = "insert into DrillingStopDetection " +selectClauseStop + " from pattern [ (every (s1=" +eventInName +" -> s2=" +eventInName +"(wob < 3)))] unidirectional, LastAvgRpmTorque.std:lastevent() as s3, DrillingStatus.std:lastevent() as s4  "
				+" where (s1.ram_pos_setpoint > s2.ram_pos_setpoint) and ((s2.rpm*s2.torque) < (s3.averageTorque*0.8)) and s4.drilling=true";
		
		String selectDrillingStart = "select * from DrillingStartDetection";
		String selectDrillingStop = "select * from DrillingStopDetection";
		
		statements.add("create schema DrillingStatus as (drilling boolean)");
		statements.add(pattern);
		statements.add(avgRpmTorquePattern);
		statements.add(lastAvgAfterDrillingStart);
		statements.add(patternDrillingStop);
		statements.add("insert into DrillingStatus select true as drilling from DrillingStartDetection");
		statements.add("insert into DrillingStatus select false as drilling from DrillingStopDetection");
		statements.add(selectDrillingStart);
		statements.add(selectDrillingStop);
		
		//statements.add(pattern2);
		return statements;
	}
	
	private String makeSelectClause(List<EventProperty> eventProperties)
	{
		String selectClause = "select '1' as drillingStatus, s2.time as drillingStartTime, ";
		for(int i = 0; i < eventProperties.size(); i++)
		{
			EventProperty p = eventProperties.get(i);
			selectClause = selectClause + "s2." +p.getRuntimeName() +" as " +p.getRuntimeName();
			if (i < (eventProperties.size() - 1)) selectClause += ", ";
		}
		return selectClause;
		
	}
	
	private String makeSelectClauseDrillingStop(List<EventProperty> eventProperties)
	{
		String selectClause = "select '0' as drillingStatus, ";
		for(int i = 0; i < eventProperties.size(); i++)
		{
			EventProperty p = eventProperties.get(i);
			selectClause = selectClause + "s2." +p.getRuntimeName() +" as " +p.getRuntimeName();
			if (i < (eventProperties.size() - 1)) selectClause += ", ";
		}
		return selectClause;
		
	}

}
