package de.fzi.cep.sepa.esper.aggregate.rate;

import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;


public class EventRate extends EsperEventEngine<EventRateParameter>{
	
	protected List<String> statements(final EventRateParameter params) {
		String outName = "`" +params.getOutName() +"`";
		String inName = "`" +params.getInName() +"`";
		String epl = "insert into " +outName +" select rate(" +params.getAvgRate() +") as rate from " +inName +" output snapshot every " +params.getOutputRate() +" sec";
		
		return makeStatementList(epl);
		
	}
}
