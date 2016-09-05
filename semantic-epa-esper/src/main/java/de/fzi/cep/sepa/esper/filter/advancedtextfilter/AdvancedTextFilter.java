package de.fzi.cep.sepa.esper.filter.advancedtextfilter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.Junction;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class AdvancedTextFilter extends EsperEventEngine<AdvancedTextFilterParameters>{

	private static final Logger logger = LoggerFactory.getLogger(AdvancedTextFilter.class.getSimpleName());
	
	
	@Override
	protected List<String> statements(
			AdvancedTextFilterParameters params) {
		
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); 
		
	
		model.whereClause(getFilterPart(params.getPropertyName(), params.getKeywords(), params.getOperation()));
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}


	private Junction getFilterPart(String propertyName, List<String> keywords, String operation) {
		Junction junction; 
		if (operation.equals("OR")) junction = Expressions.or();
		else junction = Expressions.and();
		for(int i = 0; i < keywords.size(); i++)
		{
			junction.add(Expressions.like(propertyName, "%" +keywords.get(i) +"%"));
		}
		return junction;
	}

}
