package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.factory;

import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.ANDBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.IBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.NORBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.NOTBooleanOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.ORBooleanOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.XNORBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.XORBooleanOperation;

import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.AND;
import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.NOR;
import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.NOT;
import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.OR;
import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.XOR;
import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.X_NOR;

public class BoolOperationFactory {

    public static IBoolOperation<Boolean> getBoolOperation(BooleanOperatorType type) {
        if (type == AND) {
            return new ANDBoolOperation();
        } else if (type == OR) {
            return new ORBooleanOperation();
        } else if (type == XOR) {
            return new XORBooleanOperation();
        } else if (type == NOT) {
            return new NOTBooleanOperation();
        } else if (type == X_NOR) {
            return new XNORBoolOperation();
        } else if (type == NOR) {
            return new NORBoolOperation();
        } else {
            throw new UnsupportedOperationException("Operation " + type.operator() + " is not supported");
        }
    }
}
