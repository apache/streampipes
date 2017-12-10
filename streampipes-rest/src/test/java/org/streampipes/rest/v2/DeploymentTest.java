package org.streampipes.rest.v2;

import org.streampipes.model.client.deployment.ElementType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;
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
        assertThat(Deployment.getElement(config, "{}"),instanceOf(DataSourceDescription.class));
    }

    @Test
    public void getElementSEPA() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEPA);
        assertThat(Deployment.getElement(config, "{}"),instanceOf(DataProcessorDescription.class));
    }

    @Test
    public void getElementSEC() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(ElementType.SEC);
        assertThat(Deployment.getElement(config, "{}"),instanceOf(DataSinkDescription.class));
    }

    @Test
    public void getElementNone() throws Exception {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setElementType(null);
        assertNull(Deployment.getElement(config, "{}"));
    }



}