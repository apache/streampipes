package de.fzi.cep.sepa.rest.v2;

import de.fzi.cep.sepa.model.client.deployment.ElementType;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;

public class DeploymentImplTest {
    @Test
    public void getElementSEP() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEP);
        assertThat(DeploymentImpl.getElement(config, "{}"),instanceOf(SepDescription.class));
    }

    @Test
    public void getElementSEPA() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEPA);
        assertThat(DeploymentImpl.getElement(config, "{}"),instanceOf(SepaDescription.class));
    }

    @Test
    public void getElementSEC() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEC);
        assertThat(DeploymentImpl.getElement(config, "{}"),instanceOf(SecDescription.class));
    }

    @Test
    public void getElementNone() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(null);
        assertNull(DeploymentImpl.getElement(config, "{}"));
    }



}