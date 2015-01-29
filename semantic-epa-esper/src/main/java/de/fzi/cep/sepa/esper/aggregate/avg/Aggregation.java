package de.fzi.cep.sepa.esper.aggregate.avg;

import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class Aggregation extends EsperEventEngine<AggregationParameter>{

	@Override
	protected List<String> statements(AggregationParameter bindingParameters) {
		String aggregationType = "";
		if (bindingParameters.getAggregationType() == AggregationType.AVG)
		{
			aggregationType = "avg("; 
		}
		else if (bindingParameters.getAggregationType() == AggregationType.COUNT)
		{
			aggregationType = "sum("; 
		}
		else if (bindingParameters.getAggregationType() == AggregationType.MIN)
		{
			aggregationType = "min("; 
		}
		else 
		{
			aggregationType = "max("; 
		}
		
		aggregationType = aggregationType +bindingParameters.getGroupBy() +")";  
		
		String statement = "insert into " +fixEventName(bindingParameters.getOutName()) +" select " +aggregationType +" as averageValue from " +fixEventName(bindingParameters.getInName()) +".win:time(" +bindingParameters.getTimeWindowSize() +" sec) output snapshot every " +bindingParameters.getOutputEvery() +" seconds";
		return makeStatementList(statement);
	}

}
