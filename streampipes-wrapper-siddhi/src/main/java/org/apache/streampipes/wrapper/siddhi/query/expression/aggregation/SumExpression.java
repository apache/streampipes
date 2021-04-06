package org.apache.streampipes.wrapper.siddhi.query.expression.aggregation;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpressionBase;

public class SumExpression  extends PropertyExpressionBase {

    private PropertyExpression propertyExpression;

    public SumExpression(PropertyExpression property) {
        this.propertyExpression = property;
    }

    @Override
    public String toSiddhiEpl() {
        return join(SiddhiConstants.EMPTY,
                AggregationFunction.SUM.toAggregationFunction(),
                SiddhiConstants.PARENTHESIS_OPEN,
                propertyExpression.toSiddhiEpl(),
                SiddhiConstants.PARENTHESIS_CLOSE);
    }
}
