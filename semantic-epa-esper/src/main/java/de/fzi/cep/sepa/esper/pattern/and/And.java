package de.fzi.cep.sepa.esper.pattern.and;

import java.util.List;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.PatternEveryExpr;
import com.espertech.esper.client.soda.PatternExpr;
import com.espertech.esper.client.soda.PatternStream;
import com.espertech.esper.client.soda.Patterns;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.runtime.param.InputStreamParameters;

public class And extends EsperEventEngine<AndParameters> {

	@Override
	protected List<String> statements(AndParameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(createSelect(params));
						 
		InputStreamParameters leftStream = params.getInputStreamParams().get(0);
		InputStreamParameters rightStream = params.getInputStreamParams().get(1);
		
		PatternExpr p1 = Patterns.filter(fixEventName(leftStream.getInName()), "a");
		PatternExpr p2 = Patterns.filter(fixEventName(rightStream.getInName()), "b");
		
		PatternExpr pattern = Patterns.timerWithin(params.getDuration(), Patterns.and(p1, p2));
		PatternEveryExpr everyExpr = Patterns.every(pattern);
		model.setFromClause(FromClause.create(PatternStream.create(everyExpr)));
		
//		Expression left = Expressions.property("a." +params.getMatchingProperties().get(0));
//		Expression right = Expressions.property("b." +params.getMatchingProperties().get(1));
//		
//		Expression matchingExpr;
//		switch(params.getMatchingOperator()) {
//			case "<=" : matchingExpr = Expressions.le(left, right);
//			case "<" : matchingExpr = Expressions.lt(left, right);
//			case ">" : matchingExpr = Expressions.gt(left, right);
//			case ">=" : matchingExpr = Expressions.ge(left, right);
//			default : matchingExpr = Expressions.eq(left, right);
//		}
		//model.setWhereClause(matchingExpr);
		
		return makeStatementList(model.toEPL());
	}

	private SelectClause createSelect(AndParameters params) {
		SelectClause selectClause = SelectClause.create();
		
		for(int i = 0; i < params.getGraph().getOutputStream().getEventSchema().getEventProperties().size(); i++) {
			EventProperty property = params.getGraph().getOutputStream().getEventSchema().getEventProperties().get(i);
			selectClause.add(Expressions.property(findNewRuntimeName(params.getGraph().getInputStreams(), property.getRdfId().toString())), property.getRuntimeName());
		}	
		
		return selectClause;
	}

	private String findNewRuntimeName(List<EventStream> inputStreams, String rdfId) {
		String matchedProperty = null;
		for(int i = 0; i < inputStreams.size(); i++) {
			EventStream stream = inputStreams.get(i);		
			for(int j = 0; j < stream.getEventSchema().getEventProperties().size(); j++) {
				EventProperty p = stream.getEventSchema().getEventProperties().get(j);
				if (p.getRdfId().toString().equals(rdfId)) matchedProperty = getPrefix(i) +p.getRuntimeName();
			}
		}
		if (matchedProperty == null) return findNewRuntimeName(inputStreams, rdfId.substring(0, rdfId.length()-1));
		return matchedProperty;
	}

	private String getPrefix(int i) {
		return (i == 0) ? "a." : "b.";
	}

}
