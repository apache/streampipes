package org.streampipes.rest.v2;

import org.streampipes.model.client.deployment.ElementType;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.rest.impl.Deployment;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import org.streampipes.model.client.deployment.DeploymentConfiguration;

public class DeploymentTest {
    @Test
    public void getElementSEP() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEP);
        assertThat(Deployment.getElement(config, "{}"),instanceOf(SepDescription.class));
    }

    @Test
    public void getElementSEPA() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEPA);
        assertThat(Deployment.getElement(config, "{}"),instanceOf(SepaDescription.class));
    }

    @Test
    public void getElementSEC() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEC);
        assertThat(Deployment.getElement(config, "{}"),instanceOf(SecDescription.class));
    }

    @Test
    public void getElementNone() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(null);
        assertNull(Deployment.getElement(config, "{}"));
    }



}