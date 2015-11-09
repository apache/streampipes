package de.fzi.cep.sepa.esper.collection;

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

public class TestCollection extends EsperEventEngine<TestCollectionParameters>{

	private static final Logger logger = LoggerFactory.getLogger(TestCollection.class.getSimpleName());
	
	@Override
	protected List<String> statements(TestCollectionParameters params) {
		
		// select *, 'label' from X where
		
		List<String> statements = new ArrayList<String>();
		
		
		for(DataRange range : params.getDomainConceptData())
		{
			EPStatementObjectModel model = new EPStatementObjectModel();
			model.selectClause(SelectClause.createWildcard());
			model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); 
				
			model.whereClause(getWhereClause(params.getPropertyName(), range.getMin(), range.getMax()));
			
			logger.info("Generated EPL: " +model.toEPL());
			
			statements.add(model.toEPL());
		}
		
		return statements;
	}
	
	private Expression getWhereClause(String propertyName, int minValue, int maxValue)
	{
		return Expressions.between(Expressions.property(propertyName), Expressions.constant(minValue), Expressions.constant(maxValue));
	}

}
