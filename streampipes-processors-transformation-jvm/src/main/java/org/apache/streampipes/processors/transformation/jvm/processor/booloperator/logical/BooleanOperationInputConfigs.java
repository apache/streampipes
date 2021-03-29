package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical;

import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType;

import java.util.List;

public class BooleanOperationInputConfigs {
    private List<String> properties;
    private BooleanOperatorType operator;

    public BooleanOperationInputConfigs(List<String> properties, BooleanOperatorType operatorType) {
        this.properties = properties;
        this.operator = operatorType;
    }

    public BooleanOperatorType getOperator() {
        return operator;
    }

    public List<String> getProperties() {
        return properties;
    }
}
