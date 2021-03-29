package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums;

public enum BooleanOperatorType {

    XOR("XOR", "Performs XOR between boolean operands"),
    OR("OR", "Performs OR between boolean operands"),
    AND("AND", "Performs AND between boolean operands"),
    NOT("NOT", "Performs NOT on boolean operand"),
    X_NOR("X-NOR", "Performs X-NOR between boolean operands"),
    NOR("NOR","Performs NOR between boolean operands" );

    private final String operator;
    private final String description;

    BooleanOperatorType(String operator, String description) {
        this.operator = operator;
        this.description = description;
    }

    public String operator()
    {
        return operator;
    }

    public String description() {
        return description;
    }

}
