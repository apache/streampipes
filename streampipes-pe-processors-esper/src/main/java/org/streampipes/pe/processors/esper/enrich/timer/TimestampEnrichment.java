package org.streampipes.pe.processors.esper.enrich.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;

import org.streampipes.pe.processors.esper.EsperEventEngine;

public class TimestampEnrichment extends EsperEventEngine<TimestampParameter> {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	@Override
	protected List<String> statements(TimestampParameter bindingParameters) {
		
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(bindingParameters));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(bindingParameters.getInputStreamParams().get(0).getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}

	private SelectClause makeSelectClause(TimestampParameter bindingParameters) {
		SelectClause clause = SelectClause.create();
		for(String property : bindingParameters.getPropertyNames())
		{
			clause.add(property);
		}
		clause.addWithAsProvidedName("current_timestamp", bindingParameters.getAppendTimePropertyName());
		return clause;
	}

}
