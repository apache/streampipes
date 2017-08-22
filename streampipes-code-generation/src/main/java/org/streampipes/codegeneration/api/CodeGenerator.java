package org.streampipes.codegeneration.api;

import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.client.deployment.OutputType;
import org.streampipes.model.client.deployment.RuntimeType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;

public abstract class CodeGenerator {
    protected ConsumableSEPAElement element;
    protected DeploymentConfiguration config;

    public CodeGenerator(DeploymentConfiguration config, ConsumableSEPAElement element) {
        this.config = config;
        this.element = element;
    }

    public static ImplementationCodeGenerator getCodeGenerator(DeploymentConfiguration config, NamedSEPAElement element) {

       if (config.getOutputType() == OutputType.IMPLEMENTATION) {
            if (config.getRuntimeType() == RuntimeType.FLINK) {
                if (element instanceof SepaDescription) {
                    return new FlinkSepaCodeGenerator(config, (SepaDescription) element, isStandalone(config));
                }
            }
        } else if (config.getOutputType() == OutputType.DESCRIPTION) {
        		return new FlinkSepaCodeGenerator(config, (SepaDescription) element, isStandalone(config));
    	}
    	return null;
	}

    private static boolean isStandalone(DeploymentConfiguration config) {
        return config.getOutputType() == OutputType.IMPLEMENTATION;
    }
}
