package de.fzi.cep.sepa.esper.classification.number;

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

public class NumberClassification extends EsperEventEngine<NumberClassificationParameters>{
	private static final Logger logger = LoggerFactory.getLogger(NumberClassification.class.getSimpleName());

	@Override
	protected List<String> statements(NumberClassificationParameters bindingParameters) {
		
		List<String> statements = new ArrayList<String>();
		
		for(DataClassification data : bindingParameters.getDomainConceptData()) {
			EPStatementObjectModel model = new EPStatementObjectModel();
			model.selectClause(makeSelectClause(data));
			model.fromClause(new FromClause().add(FilterStream.create(fixEventName(bindingParameters.getInputStreamParams().get(0).getInName())))); 

			model.whereClause(getWhereClause(bindingParameters.getPropertyName(), data.getMinValue(), data.getMaxValue()));
			logger.info("Generated EPL: " +model.toEPL());
			
			statements.add(model.toEPL());
		}

		return statements;
	}

	private SelectClause makeSelectClause(DataClassification dataClassification) {
		SelectClause clause = SelectClause.create();
//		for(String property : bindingParameters.getPropertyNames()) {
//			clause.add(property);
//		}
		
		//TODO change label to dynamic 
		clause.addWithAsProvidedName("label", dataClassification.getLabel());
		return clause;
	}
	
	private Expression getWhereClause(String propertyName, int minValue, int maxValue)
	{
		return Expressions.between(Expressions.property(propertyName), Expressions.constant(minValue), Expressions.constant(maxValue));
	}

}
