package org.apache.streampipes.commons.environment.variable;

import org.apache.streampipes.commons.constants.Envs;

public class DoubleEnvironmentVariable extends EnvironmentVariable<Double>{

    public DoubleEnvironmentVariable(Envs envVariable) {
        super(envVariable);
    }

    @Override
    public Double parse(String value) {
        return Double.parseDouble(value.toLowerCase());
    }
}
