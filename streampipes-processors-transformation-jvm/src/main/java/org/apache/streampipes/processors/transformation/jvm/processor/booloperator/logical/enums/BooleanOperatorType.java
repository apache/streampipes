package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums;

public enum BooleanOperatorType {

    XOR("XOR", "Performs XOR between boolean operands"),
    OR("OR", "Performs OR between boolean operands"),
    AND("AND", "Performs AND between boolean operands"),
    NOT("NOT", "Performs NOT on boolean operand"),
    X_NOR("X-NOR", "Performs X-NOR between boolean operands"),
    NOR("NOR", "Performs NOR between boolean operands");

    private final String operator;
    private final String description;

    BooleanOperatorType(String operator, String description) {
        this.operator = operator;
        this.description = description;
    }

    public String operator() {
        return operator;
    }

    public String description() {
        return description;
    }

    public static BooleanOperatorType getBooleanOperatorType(String operator) {
        if (operator.equals(AND.operator())) {
            return AND;
        } else if (operator.equals(OR.operator())) {
            return OR;
        } else if (operator.equals(XOR.operator())) {
            return XOR;
        } else if (operator.equals(NOT.operator())) {
            return NOT;
        } else if (operator.equals(X_NOR.operator())) {
            return X_NOR;
        } else if (operator.equals(NOR.operator())) {
            return NOR;
        } else {
            throw new UnsupportedOperationException("No match found for " + operator);
        }
    }

}
