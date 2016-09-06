package de.fzi.cep.sepa.esper.filter.text;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.esper.util.StringOperator;

public class TextFilter extends EsperEventEngine<TextFilterParameter>{
	
	private static final Logger logger = LoggerFactory.getLogger(TextFilter.class.getSimpleName());
	
	protected List<String> statements(final TextFilterParameter params) {
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); // in name
		
		Expression stringFilter;
		if (params.getStringOperator() == StringOperator.MATCHES)
			stringFilter = Expressions.eq(params.getFilterProperty(), params.getKeyword());
		else
			stringFilter = Expressions.like(params.getFilterProperty(), "%" +params.getKeyword() +"%");
	
		model.whereClause(stringFilter);
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
		
	}
}
