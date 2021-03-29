package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations;

public class NOTBooleanOperation implements IBoolOperation<Boolean> {
    @Override
    public Boolean evaluate(Boolean operand, Boolean sameOperand) {
        return !operand;
    }
}
