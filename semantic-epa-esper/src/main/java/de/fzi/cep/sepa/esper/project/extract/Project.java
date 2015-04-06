package de.fzi.cep.sepa.esper.project.extract;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class Project extends EsperEventEngine<ProjectParameter>{

	private static final Logger logger = Logger.getLogger(Project.class);
	
	@Override
	protected List<String> statements(ProjectParameter params) {
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(params));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInName())))); // in name
		
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
