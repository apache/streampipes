package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations;

/**
 * Represents a logical boolean operation.
 */
public interface IBoolOperation<T extends Boolean> {
    /**
     *
     * Performs the logical boolean operation b/w operands and returns the result
     * @param operand first boolean operand.
     * @param otherOperand other boolean operand.
     * @return  Result of boolean operation.
     *
     */
    T evaluate(T operand, T otherOperand);
}
