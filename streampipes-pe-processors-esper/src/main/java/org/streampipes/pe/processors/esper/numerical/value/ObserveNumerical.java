package org.streampipes.pe.processors.esper.numerical.value;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;
import org.streampipes.wrapper.esper.EsperEventEngine;

import java.util.ArrayList;
import java.util.List;

public class ObserveNumerical extends EsperEventEngine<ObserveNumericalParameters> {
	private static String UPPER_LIMIT =  "Upper Limit";

	@Override
	protected List<String> statements(ObserveNumericalParameters bindingParameters) {
		List<String> statements = new ArrayList<String>();
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.selectClause(makeSelectClause(bindingParameters));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(bindingParameters.getInputStreamParams().get(0).getInName())))); 
		model.whereClause(getWhereClause(bindingParameters));

		statements.add(model.toEPL());

		return statements;
	}

	private SelectClause makeSelectClause(ObserveNumericalParameters bindingParameters) {
		SelectClause clause = SelectClause.create();
		for (String property : bindingParameters.getInputStreamParams().get(0).getAllProperties()) {
			clause.add(property);
		}

		String message = "";
		if (bindingParameters.getValueLimit().equals(UPPER_LIMIT)) {
			message = "Value of " + bindingParameters.getNumber() + " too high";
		} else {
			message = "Value of " + bindingParameters.getNumber() + " too low";
		}

		clause.add(Expressions.constant(message), bindingParameters.getOutputProperty());
		return clause;
	}
	
	private Expression getWhereClause(ObserveNumericalParameters params) {

		if (params.getValueLimit().equals(UPPER_LIMIT)) {
			return Expressions.gt(convert(params.getNumber()), Expressions.constant(params.getThreshold()));
		} else {
			return Expressions.lt(convert(params.getNumber()), Expressions.constant(params.getThreshold()));
		}
	}

	private Expression convert(String property)
	{
		return Expressions.cast(property, "double");
	}
}
