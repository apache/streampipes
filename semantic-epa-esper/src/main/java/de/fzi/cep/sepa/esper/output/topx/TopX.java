package de.fzi.cep.sepa.esper.output.topx;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.esper.project.extract.NestedPropertyMapping;

public class TopX extends EsperEventEngine<TopXParameter>{

	private static final Logger logger = Logger.getAnonymousLogger();
	
	@Override
	protected List<String> statements(TopXParameter bindingParameters) {
		
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(bindingParameters));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(bindingParameters.getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}

	private SelectClause makeSelectClause(TopXParameter bindingParameters) {
		SelectClause clause = SelectClause.create();
		clause.addWildcard();
		return clause;
	}

}
