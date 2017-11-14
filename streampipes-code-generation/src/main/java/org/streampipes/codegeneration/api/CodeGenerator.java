package org.streampipes.codegeneration.api;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.client.deployment.OutputType;
import org.streampipes.model.client.deployment.RuntimeType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;

public abstract class CodeGenerator {
    protected ConsumableStreamPipesEntity element;
    protected DeploymentConfiguration config;

    public CodeGenerator(DeploymentConfiguration config, ConsumableStreamPipesEntity element) {
        this.config = config;
        this.element = element;
    }

    public static ImplementationCodeGenerator getCodeGenerator(DeploymentConfiguration config, NamedStreamPipesEntity element) {

       if (config.getOutputType() == OutputType.IMPLEMENTATION) {
            if (config.getRuntimeType() == RuntimeType.FLINK) {
                if (element instanceof DataProcessorDescription) {
                    return new FlinkSepaCodeGenerator(config, (DataProcessorDescription) element, isStandalone(config));
                }
            }
        } else if (config.getOutputType() == OutputType.DESCRIPTION) {
        		return new FlinkSepaCodeGenerator(config, (DataProcessorDescription) element, isStandalone(config));
    	}
    	return null;
	}

    private static boolean isStandalone(DeploymentConfiguration config) {
        return config.getOutputType() == OutputType.IMPLEMENTATION;
    }
}
