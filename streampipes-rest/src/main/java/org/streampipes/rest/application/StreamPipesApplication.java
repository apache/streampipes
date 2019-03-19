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

import org.streampipes.rest.impl.ApplicationLink;
import org.streampipes.rest.impl.Authentication;
import org.streampipes.rest.impl.AutoComplete;
import org.streampipes.rest.impl.ConsulConfig;
import org.streampipes.rest.impl.ContainerProvidedOptions;
import org.streampipes.rest.impl.Couchdb;
import org.streampipes.rest.impl.DataStream;
import org.streampipes.rest.impl.Deployment;
import org.streampipes.rest.impl.InternalPipelineTemplates;
import org.streampipes.rest.impl.Notification;
import org.streampipes.rest.impl.OntologyContext;
import org.streampipes.rest.impl.OntologyKnowledge;
import org.streampipes.rest.impl.OntologyMeasurementUnit;
import org.streampipes.rest.impl.OntologyPipelineElement;
import org.streampipes.rest.impl.PipelineCategory;
import org.streampipes.rest.impl.PipelineElementAsset;
import org.streampipes.rest.impl.PipelineElementCategory;
import org.streampipes.rest.impl.PipelineElementImport;
import org.streampipes.rest.impl.PipelineElementRuntimeInfo;
import org.streampipes.rest.impl.PipelineTemplate;
import org.streampipes.rest.impl.PipelineWithUserResource;
import org.streampipes.rest.impl.RdfEndpoint;
import org.streampipes.rest.impl.SemanticEventConsumer;
import org.streampipes.rest.impl.SemanticEventProcessingAgent;
import org.streampipes.rest.impl.SemanticEventProducer;
import org.streampipes.rest.impl.Setup;
import org.streampipes.rest.impl.StreamPipesLogs;
import org.streampipes.rest.impl.User;
import org.streampipes.rest.impl.Version;
import org.streampipes.rest.impl.VirtualSensor;
import org.streampipes.rest.impl.Visualization;
import org.streampipes.rest.impl.nouser.PipelineElementImportNoUser;
import org.streampipes.rest.impl.nouser.PipelineNoUserResource;
import org.streampipes.rest.shared.serializer.GsonClientModelProvider;
import org.streampipes.rest.shared.serializer.GsonWithIdProvider;
import org.streampipes.rest.shared.serializer.GsonWithoutIdProvider;
import org.streampipes.rest.shared.serializer.JsonLdProvider;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

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
        apiClasses.add(PipelineWithUserResource.class);
        apiClasses.add(PipelineNoUserResource.class);
        apiClasses.add(PipelineElementImportNoUser.class);
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
        apiClasses.add(StreamPipesLogs.class);
        apiClasses.add(PipelineTemplate.class);
        apiClasses.add(Couchdb.class);
        apiClasses.add(InternalPipelineTemplates.class);
        apiClasses.add(PipelineElementRuntimeInfo.class);
        apiClasses.add(Version.class);
        apiClasses.add(PipelineElementAsset.class);


        // Serializers
        apiClasses.add(GsonWithIdProvider.class);
        apiClasses.add(GsonWithoutIdProvider.class);
        apiClasses.add(GsonClientModelProvider.class);
        apiClasses.add(JsonLdProvider.class);

        return apiClasses;

    }
}
