package de.fzi.cep.sepa.streampipes.codegeneration.api;

import de.fzi.cep.sepa.model.client.deployment.*;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.streampipes.codegeneration.flink.sepa.FlinkSepaCodeGenerator;
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