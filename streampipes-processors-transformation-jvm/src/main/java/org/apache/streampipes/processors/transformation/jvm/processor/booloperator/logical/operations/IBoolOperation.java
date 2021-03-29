package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations;

public interface IBoolOperation<T extends Boolean> {
    T evaluate(T operand, T otherOperand);
}
