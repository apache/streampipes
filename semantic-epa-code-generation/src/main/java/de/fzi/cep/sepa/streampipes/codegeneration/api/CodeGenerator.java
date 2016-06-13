package de.fzi.cep.sepa.streampipes.codegeneration.api;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.client.deployment.OutputType;
import de.fzi.cep.sepa.model.client.deployment.RuntimeType;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;

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
