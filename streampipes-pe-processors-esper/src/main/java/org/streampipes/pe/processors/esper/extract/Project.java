package org.streampipes.pe.processors.esper.extract;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.wrapper.esper.EsperEventEngine;

import java.util.ArrayList;
import java.util.List;

public class Project extends EsperEventEngine<ProjectParameter> {

	private static final Logger logger = LoggerFactory.getLogger(Project.class);
	
	@Override
	protected List<String> statements(ProjectParameter params) {
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(params));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}

	private SelectClause makeSelectClause(ProjectParameter params) {
		SelectClause clause = SelectClause.create();
		for(NestedPropertyMapping property : params.getProjectProperties())
		{
			clause.addWithAsProvidedName(property.getFullPropertyName(), property.getPropertyName());
		}
		return clause;
	}
	
	

}
