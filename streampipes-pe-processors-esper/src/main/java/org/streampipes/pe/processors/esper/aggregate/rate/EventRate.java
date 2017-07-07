package org.streampipes.pe.processors.esper.aggregate.rate;

import java.util.List;

import org.streampipes.pe.processors.esper.EsperEventEngine;


public class EventRate extends EsperEventEngine<EventRateParameter> {
	
	protected List<String> statements(final EventRateParameter params) {
		String inName = "`" +params.getInputStreamParams().get(0).getInName() +"`";
		String epl = "select rate(" +params.getAvgRate() +") as rate from " +inName +" output snapshot every " +params.getOutputRate() +" sec";
		
		return makeStatementList(epl);
		
	}
}
