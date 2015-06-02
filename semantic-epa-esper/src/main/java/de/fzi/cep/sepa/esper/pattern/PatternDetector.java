package de.fzi.cep.sepa.esper.pattern;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.PatternAndExpr;
import com.espertech.esper.client.soda.PatternStream;
import com.espertech.esper.client.soda.Patterns;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class PatternDetector extends EsperEventEngine<PatternParameters> {

	private static final Logger logger = LoggerFactory.getLogger(PatternDetector.class.getSimpleName());
	
	protected List<String> statements(final PatternParameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		
		SepaInvocation graph = params.getGraph();
		
		PatternAndExpr pattern = Patterns.and();
				 
		EventStream leftStream = graph.getInputStreams().get(0);
		EventStream rightStream = graph.getInputStreams().get(1);
		
		pattern.add(Patterns.everyFilter(leftStream.getName(), rightStream.getName()));
		pattern.add(Patterns.everyFilter(rightStream.getName(), rightStream.getName()));
		model.setFromClause(FromClause.create(PatternStream.create(pattern)));
		
		System.out.println(model.toEPL());
		return makeStatementList(model.toEPL());
	}
}
