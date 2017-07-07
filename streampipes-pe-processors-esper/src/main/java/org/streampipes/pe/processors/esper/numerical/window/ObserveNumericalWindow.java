package org.streampipes.pe.processors.esper.numerical.window;

import java.util.List;

import org.streampipes.pe.processors.esper.EsperEventEngine;

public class ObserveNumericalWindow extends EsperEventEngine<ObserveNumericalWindowParameters> {

	private static String UPPER_LIMIT = "Upper Limit";
	private static String TIME = "Time [sec]";

	@Override
	protected List<String> statements(ObserveNumericalWindowParameters bindingParameters) {

		String statement = selectClause(bindingParameters) + fromClause(bindingParameters)
				+ groupByClause(bindingParameters) + havingClause(bindingParameters);

		System.out.println(statement);

		return makeStatementList(statement);
	}

	private String selectClause(ObserveNumericalWindowParameters bindingParameters) {
		String select = "select ";

		for (String property : bindingParameters.getInputStreamParams().get(0).getAllProperties()) {
			select = select + "`" + property + "`" + ", ";
		}

		select = select + getMessage(bindingParameters) + " as " + bindingParameters.getMessageName() + ", ";

		return select + "avg(cast(" + bindingParameters.getToObserve() + ", double)) as "
				+ bindingParameters.getAverageName();
	}

	private String fromClause(ObserveNumericalWindowParameters bindingParameters) {
		return " from " + fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()) + getWindowType(bindingParameters);
	}

	private String groupByClause(ObserveNumericalWindowParameters bindingParameters) {
		return " group by `" + bindingParameters.getGroupBy();
	}

	private String havingClause(ObserveNumericalWindowParameters bindingParameters) {
		return "` having avg(cast(" + bindingParameters.getToObserve() + ", double))" + getOperator(bindingParameters)
				+ bindingParameters.getThreshold() + getValueLimit(bindingParameters) + " `"
				+ bindingParameters.getToObserve() + "`";
	}
	
	private String getWindowType(ObserveNumericalWindowParameters bindingParameters) {
		if (TIME.equals(bindingParameters.getSlidingWindowType())) {
			return ".win:time(" + bindingParameters.getWindowTime() + " sec) ";
		} else {
			return ".win:length(" + bindingParameters.getWindowTime() + ") ";
		}
	}

	private String getValueLimit(ObserveNumericalWindowParameters bindingParameters) {
		if (UPPER_LIMIT.equals(bindingParameters.getValueLimit())) {
			return "<";
		} else {
			return ">";
		}
	}

	private String getMessage(ObserveNumericalWindowParameters bindingParameters) {
		if (UPPER_LIMIT.equals(bindingParameters.getValueLimit())) {
			return "'Value of " + bindingParameters.getToObserve() + " too high'";
		} else {
			return "'Value of " + bindingParameters.getToObserve() + " too low'";
		}
	}

	private String getOperator(ObserveNumericalWindowParameters bindingParameters) {
		if (UPPER_LIMIT.equals(bindingParameters.getValueLimit())) {
			return " + ";
		} else {
			return " - ";
		}
	}
}
