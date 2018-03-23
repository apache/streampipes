/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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