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

package org.streampipes.backend;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.streampipes.rest.impl.ApplicationLink;
import org.streampipes.rest.impl.AssetDashboard;
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
import org.streampipes.rest.impl.PipelineElementFile;
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
import org.streampipes.rest.impl.datalake.DataLakeNoUserResourceV3;
import org.streampipes.rest.impl.datalake.DataLakeResourceV3;
import org.streampipes.rest.impl.nouser.FileServingResource;
import org.streampipes.rest.impl.nouser.PipelineElementImportNoUser;
import org.streampipes.rest.impl.nouser.PipelineNoUserResource;
import org.streampipes.rest.shared.serializer.GsonClientModelProvider;
import org.streampipes.rest.shared.serializer.GsonWithIdProvider;
import org.streampipes.rest.shared.serializer.GsonWithoutIdProvider;
import org.streampipes.rest.shared.serializer.JsonLdProvider;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class StreamPipesResourceConfig extends ResourceConfig {

  public StreamPipesResourceConfig() {
    register(Authentication.class);
    register(Authentication.class);
    register(AssetDashboard.class);
    register(AutoComplete.class);
    register(PipelineElementCategory.class);
    register(Deployment.class);
    register(Notification.class);
    register(OntologyContext.class);
    register(OntologyKnowledge.class);
    register(OntologyMeasurementUnit.class);
    register(OntologyPipelineElement.class);
    register(PipelineWithUserResource.class);
    register(PipelineNoUserResource.class);
    register(PipelineElementImportNoUser.class);
    register(PipelineCategory.class);
    register(PipelineElementImport.class);
    register(SemanticEventConsumer.class);
    register(SemanticEventProcessingAgent.class);
    register(SemanticEventProducer.class);
    register(Setup.class);
    register(VirtualSensor.class);
    register(Visualization.class);
    register(RdfEndpoint.class);
    register(ApplicationLink.class);
    register(User.class);
    register(ConsulConfig.class);
    register(DataStream.class);
    register(ContainerProvidedOptions.class);
    register(StreamPipesLogs.class);
    register(PipelineTemplate.class);
    register(Couchdb.class);
    register(InternalPipelineTemplates.class);
    register(PipelineElementRuntimeInfo.class);
    register(Version.class);
    register(PipelineElementAsset.class);
    register(DataLakeResourceV3.class);
    register(DataLakeNoUserResourceV3.class);
    register(PipelineElementFile.class);
    register(FileServingResource.class);


    // Serializers
    register(GsonWithIdProvider.class);
    register(GsonWithoutIdProvider.class);
    register(GsonClientModelProvider.class);
    register(JsonLdProvider.class);

    register(MultiPartFeature.class);
  }

}
