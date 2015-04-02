package de.fzi.cep.sepa.esper.enrich.math;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class Math extends EsperEventEngine<MathParameter>{

	private static final Logger logger = Logger.getAnonymousLogger();
	
	@Override
	protected List<String> statements(MathParameter bindingParameters) {
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.selectClause(makeSelectClause(bindingParameters));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(bindingParameters.getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}

	private SelectClause makeSelectClause(MathParameter bindingParameters) {
		
		Operation selectedOperation = bindingParameters.getOperation();
		String asName = bindingParameters.getAppendPropertyName();
		
		SelectClause clause = SelectClause.create();
		for(String property : bindingParameters.getSelectProperties())
		{
			clause.add(property);
		}
		Expression left = Expressions.property(bindingParameters.getLeftOperand());
		Expression right = Expressions.property(bindingParameters.getRightOperand());
		
		if (selectedOperation == Operation.ADD) clause.add(Expressions.plus(left, right), asName);
		else if (selectedOperation == Operation.SUBTRACT) clause.add(Expressions.minus(left, right), asName);
		else if (selectedOperation == Operation.MULTIPLY) clause.add(Expressions.multiply(left, right), asName);
		else clause.add(Expressions.divide(left, right), asName);
		
		return clause;
	}
	
}
