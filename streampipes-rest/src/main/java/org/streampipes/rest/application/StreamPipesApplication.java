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

package org.streampipes.rest.application;

import org.streampipes.rest.impl.*;
import org.streampipes.rest.impl.connect.GuessResource;
import org.streampipes.rest.impl.connect.SpConnect;
import org.streampipes.rest.serializer.GsonClientModelProvider;
import org.streampipes.rest.serializer.GsonWithIdProvider;
import org.streampipes.rest.serializer.GsonWithoutIdProvider;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class StreamPipesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> apiClasses = new HashSet<>();

        // APIs
        apiClasses.add(Authentication.class);
        apiClasses.add(AutoComplete.class);
        apiClasses.add(PipelineElementCategory.class);
        apiClasses.add(Deployment.class);
        apiClasses.add(Notification.class);
        apiClasses.add(OntologyContext.class);
        apiClasses.add(OntologyKnowledge.class);
        apiClasses.add(OntologyMeasurementUnit.class);
        apiClasses.add(OntologyPipelineElement.class);
        apiClasses.add(Pipeline.class);
        apiClasses.add(PipelineCategory.class);
        apiClasses.add(PipelineElementImport.class);
        apiClasses.add(SemanticEventConsumer.class);
        apiClasses.add(SemanticEventProcessingAgent.class);
        apiClasses.add(SemanticEventProducer.class);
        apiClasses.add(Setup.class);
        apiClasses.add(VirtualSensor.class);
        apiClasses.add(Visualization.class);
        apiClasses.add(RdfEndpoint.class);
        apiClasses.add(ApplicationLink.class);
        apiClasses.add(User.class);
        apiClasses.add(ConsulConfig.class);
        apiClasses.add(DataStream.class);
        apiClasses.add(ContainerProvidedOptions.class);
        apiClasses.add(Logs.class);
        apiClasses.add(SpConnect.class);
        apiClasses.add(GuessResource.class);
        apiClasses.add(PipelineTemplate.class);
        apiClasses.add(Couchdb.class);
        apiClasses.add(LogPipelineTemplates.class);


        // Serializers
        apiClasses.add(GsonWithIdProvider.class);
        apiClasses.add(GsonWithoutIdProvider.class);
        apiClasses.add(GsonClientModelProvider.class);

        return apiClasses;

    }
}
