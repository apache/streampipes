package de.fzi.cep.sepa.esper.pattern.sequence;

import java.util.List;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.PatternEveryExpr;
import com.espertech.esper.client.soda.PatternExpr;
import com.espertech.esper.client.soda.PatternStream;
import com.espertech.esper.client.soda.Patterns;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.runtime.param.InputStreamParameters;

public class Sequence extends EsperEventEngine<SequenceParameters> {

	@Override
	protected List<String> statements(SequenceParameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(SelectClause.createWildcard());
						 
		InputStreamParameters leftStream = params.getInputStreamParams().get(0);
		InputStreamParameters rightStream = params.getInputStreamParams().get(1);
		
		PatternEveryExpr p1 = Patterns.everyFilter(fixEventName(leftStream.getInName()), "a");
		PatternEveryExpr p2 = Patterns.everyFilter(fixEventName(rightStream.getInName()), "b");
		
		PatternExpr pattern = Patterns.timerWithin(1, Patterns.followedBy(p1, p2));
				
		model.setFromClause(FromClause.create(PatternStream.create(pattern)));
		
		Expression left = Expressions.property("a." +params.getMatchingProperties().get(0));
		Expression right = Expressions.property("b." +params.getMatchingProperties().get(1));
		
		Expression matchingExpr;
		switch(params.getMatchingOperator()) {
			case "<=" : matchingExpr = Expressions.le(left, right);
			case "<" : matchingExpr = Expressions.lt(left, right);
			case ">" : matchingExpr = Expressions.gt(left, right);
			case ">=" : matchingExpr = Expressions.ge(left, right);
			default : matchingExpr = Expressions.eq(left, right);
		}
		model.setWhereClause(matchingExpr);
		
		System.out.println(model.toEPL());
		return makeStatementList(model.toEPL());
	}

}
