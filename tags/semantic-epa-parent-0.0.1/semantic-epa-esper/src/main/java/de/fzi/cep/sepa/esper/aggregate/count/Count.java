package de.fzi.cep.sepa.esper.aggregate.count;

import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class Count extends EsperEventEngine<CountParameter>{

	@Override
	protected List<String> statements(CountParameter bindingParameters) {
		String statement = "select " +getSelectClause(bindingParameters) +" count(*) as countValue from " +fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()) +".win:time(" +bindingParameters.getTimeWindow() +" " +bindingParameters.getTimeScale().value() +")" +getGroupBy(bindingParameters);
		return makeStatementList(statement);
	}
	
	private String getSelectClause(CountParameter params)
	{
		String result = "";
		for(String property : params.getSelectProperties())
		{
			result = result +property +", ";
		}
		return result;
	}
	
	private String getGroupBy(CountParameter params)
	{
		String groupByPrefix = " group by ";
		String result = "";
		List<String> groupBy = params.getGroupBy();
		for(int i = 0; i < groupBy.size(); i++)
		{
			result += groupBy.get(i);
			if (! (i == groupBy.size()-1)) result += ", ";
		}
		if (!result.equals("")) return groupByPrefix + result;
		else return result;
	}
}
