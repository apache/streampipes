package de.fzi.cep.sepa.esper.pattern.and;

import java.util.Map;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.filter.text.TextFilterParameter;
import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class ANDPatternDetector implements EPEngine<ANDPatternParameters>{

	@Override
	public void bind(EngineParameters<ANDPatternParameters> parameters,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		
	}
	
	private EPStatementObjectModel statement(final ANDPatternParameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(params.getInName()))); // in name
		
		Expression stringFilter;
		/*
		if (params.getStringOperator() == StringOperator.MATCHES)
			stringFilter = Expressions.eq(params.getFilterProperty(), params.getKeyword());
		else
			stringFilter = Expressions.like(params.getFilterProperty(), "%" +params.getKeyword() +"%");
	
		model.whereClause(stringFilter);
			System.out.println(model.toEPL());
			*/
		return model;
		
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

}
