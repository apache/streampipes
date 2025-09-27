package org.apache.streampipes.commons.environment.variable;

import org.apache.streampipes.commons.constants.Envs;

public class FloatEnvironmentVariable extends EnvironmentVariable<Float>{

    public FloatEnvironmentVariable(Envs envVariable) {
        super(envVariable);
    }

    @Override
    public Float parse(String value) {
        return Float.parseFloat(value.toLowerCase());
    }
}