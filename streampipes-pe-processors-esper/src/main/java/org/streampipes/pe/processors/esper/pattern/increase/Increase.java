package org.streampipes.pe.processors.esper.pattern.increase;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.PatternEveryExpr;
import com.espertech.esper.client.soda.PatternExpr;
import com.espertech.esper.client.soda.PatternFilterExpr;
import com.espertech.esper.client.soda.PatternStream;
import com.espertech.esper.client.soda.Patterns;
import com.espertech.esper.client.soda.RelationalOpExpression;
import com.espertech.esper.client.soda.SelectClause;
import org.streampipes.wrapper.params.InputStreamParameters;
import org.streampipes.wrapper.esper.EsperEventEngine;

import java.util.List;

public class Increase extends EsperEventEngine<IncreaseParameters> {

	@Override
	protected List<String> statements(IncreaseParameters params) {
		
		// select a.x, b.y, c.z from pattern[every a=Event -> timer:interval(TIMEWINDOW) -> b=Event] where b.value > a.value * 1.2
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(params));
						 
		InputStreamParameters leftStream = params.getInputStreamParams().get(0);
		
		PatternEveryExpr p1 = Patterns.everyFilter(fixEventName(leftStream.getInName()), "a");
		PatternFilterExpr p2 = Patterns.filter(fixEventName(leftStream.getInName()), "b");
		
		PatternExpr pattern = Patterns.timerInterval(params.getDuration());
				
		model.setFromClause(FromClause.create(PatternStream.create(Patterns.followedBy(p1, pattern, p2))));
		
		Expression whereClause = getWhereExp(params);
		
		model.setWhereClause(whereClause);
		
		System.out.println(model.toEPL());
		return makeStatementList(model.toEPL());
	}

	private SelectClause makeSelectClause(IncreaseParameters params) {
		SelectClause select = SelectClause.create();
		for(String fieldName : params.getOutputProperties()) {
			select.add(Expressions.property("b." +fieldName), fieldName);
		}
		return select;
	}
	
	private RelationalOpExpression getWhereExp(IncreaseParameters params) {
		if (params.getOperation() == Operation.INCREASE) {
			return Expressions.gt(
					Expressions.property("b." +params.getMapping()),
					Expressions.multiply(
							Expressions.property("a." +params.getMapping()),
							Expressions.constant(1 +(double) params.getIncrease()/100.0)));
		}
		else return Expressions.lt(
				Expressions.property("b." +params.getMapping()),
				Expressions.multiply(
						Expressions.property("a." +params.getMapping()),
						Expressions.constant(1 -(double) params.getIncrease()/100.0)));
	}

}
