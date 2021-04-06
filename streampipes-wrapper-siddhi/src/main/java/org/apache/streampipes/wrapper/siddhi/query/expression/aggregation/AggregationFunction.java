package org.apache.streampipes.wrapper.siddhi.query.expression.aggregation;

public enum AggregationFunction {
    SUM("sum"),
    COUNT("count"),
    DISTINCT_COUNT("distinctCount"),
    AVERAGE("avg"),
    MAX("max"),
    MIN("min"),
    MAX_FOREVER("maxForever"),
    MIN_FOREVER("minForever"),
    STANDARD_DEVIATION("stdDev"),
    AND("and"),
    OR("or"),
    UNION_SET("unionSet");

    private String aggregationFunction;
    AggregationFunction(String aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    public String toAggregationFunction() {
        return this.aggregationFunction;
    }
}
