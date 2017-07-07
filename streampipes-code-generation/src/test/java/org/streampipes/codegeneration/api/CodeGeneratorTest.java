package org.streampipes.codegeneration.api;

import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.client.deployment.DeploymentType;
import org.streampipes.model.client.deployment.OutputType;
import org.streampipes.model.client.deployment.RuntimeType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class CodeGeneratorTest {
    @Test
    public void getGeneratorImplementationFlinkSepaStandalone() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setOutputType(OutputType.IMPLEMENTATION);
        config.setRuntimeType(RuntimeType.FLINK);
        config.setDeploymentType(DeploymentType.STANDALONE);

        assertThat(CodeGenerator.getCodeGenerator(config, new SepaDescription()),instanceOf(FlinkSepaCodeGenerator.class));
    }

}