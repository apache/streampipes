package org.streampipes.pe.processors.esper.topx;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.OrderByElement;
import com.espertech.esper.client.soda.SelectClause;
import com.espertech.esper.client.soda.View;
import org.streampipes.wrapper.esper.EsperEventEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TopX extends EsperEventEngine<TopXParameter>{

	private static final Logger logger = Logger.getAnonymousLogger();
	
	@Override
	protected List<String> statements(TopXParameter bindingParameters) {
		
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(bindingParameters));
		
		FilterStream stream = FilterStream.create(fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()));
		
		List<Expression> uniqueProperties = new ArrayList<>();
		for(String propertyName : bindingParameters.getUniqueProperties())
		{
			uniqueProperties.add(Expressions.property(propertyName));
		}
	
		OrderDirection selectedDirection = bindingParameters.getOrderDirection(); 
		
		OrderByElement element = new OrderByElement();
		element.setExpression(Expressions.property(bindingParameters.getOrderByPropertyName() +selectedDirection.toEpl()));
		
		List<Expression> viewExpressions = new ArrayList<Expression>();
		for(Expression u : uniqueProperties) viewExpressions.add(u);
		viewExpressions.add(Expressions.constant(bindingParameters.getLimit()));
		viewExpressions.add(element.getExpression());
		
		View timeView = View.create("win", "time", Expressions.timePeriod(0, 0, 60, 0, 0));
		View view = View.create("ext", "rank", viewExpressions);
		stream.addView(timeView);
		stream.addView(view);		
		
		model.fromClause(new FromClause().add(stream)); // in name
		//model.setOutputLimitClause(OutputLimitClause.create(OutputLimitSelector.SNAPSHOT, Expressions.timePeriod(0, 0, 0, 1, 0)));
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}

	private SelectClause makeSelectClause(TopXParameter bindingParameters) {
		SelectClause clause = SelectClause.create();
		clause.add("distinct window(*) as list");
		//clause.addWildcard();
		return clause;
	}

}
