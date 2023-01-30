/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.backend;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.apache.streampipes.ps.*;
import org.apache.streampipes.rest.impl.*;
import org.apache.streampipes.rest.impl.admin.*;
import org.apache.streampipes.rest.impl.connect.*;
import org.apache.streampipes.rest.impl.dashboard.Dashboard;
import org.apache.streampipes.rest.impl.dashboard.DashboardWidget;
import org.apache.streampipes.rest.impl.dashboard.VisualizablePipelineResource;
import org.apache.streampipes.rest.impl.datalake.DataLakeDashboardResource;
import org.apache.streampipes.rest.impl.datalake.DataLakeWidgetResource;
import org.apache.streampipes.rest.impl.datalake.PersistedDataStreamResource;
import org.apache.streampipes.rest.impl.pe.DataProcessorResource;
import org.apache.streampipes.rest.impl.pe.DataSinkResource;
import org.apache.streampipes.rest.impl.pe.DataStreamResource;
import org.apache.streampipes.rest.shared.serializer.JacksonSerializationProvider;
import org.apache.streampipes.service.base.rest.ServiceHealthResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;
import java.util.Collections;

@Configuration
@ApplicationPath("/api")
public class StreamPipesResourceConfig extends ResourceConfig {

    public StreamPipesResourceConfig() {
        setProperties(Collections.singletonMap("jersey.config.server.response.setStatusOverSendError", true));
        register(AccountActivationResource.class);
        register(AdapterMonitoringResource.class);
        register(Authentication.class);
        register(AssetDashboardResource.class);
        register(AssetManagementResource.class);
        register(AutoComplete.class);
        register(CategoryResource.class);
        register(ConsulConfig.class);
        register(ContainerProvidedOptions.class);
        register(DashboardWidget.class);
        register(Dashboard.class);
        register(DataExportResource.class);
        register(DataImportResource.class);
        register(DataLakeImageResource.class);
        register(DataLakeResourceV3.class);
        register(DataLakeMeasureResourceV3.class);
        register(DataLakeMeasureResourceV4.class);
        register(DataStream.class);
        register(EmailConfigurationResource.class);
        register(EmailResource.class);
        register(ExtensionsServiceEndpointResource.class);
        register(FunctionsResource.class);
        register(GeneralConfigurationResource.class);
        register(GenericStorageResource.class);
        register(LabelResource.class);
        register(MeasurementUnitResource.class);
        register(Notification.class);
        register(OntologyMeasurementUnit.class);
        register(PermissionResource.class);
        register(PersistedDataStreamResource.class);
        register(PipelineCanvasMetadataCache.class);
        register(PipelineCanvasMetadataResource.class);
        register(PipelineCache.class);
        register(PipelineCategory.class);
        register(PipelineElementAsset.class);
        register(PipelineElementCategory.class);
        register(PipelineElementFile.class);
        register(PipelineElementImport.class);
        register(PipelineElementPreview.class);
        register(PipelineElementRuntimeInfo.class);
        register(PipelineMonitoring.class);
        register(PipelineResource.class);
        register(PipelineTemplate.class);
        register(DataSinkResource.class);
        register(DataProcessorResource.class);
        register(DataStreamResource.class);
        register(Setup.class);
        register(ResetResource.class);
        register(RestorePasswordResource.class);
        register(ServiceHealthResource.class);
        register(UserResource.class);
        register(Version.class);
        register(PipelineElementAsset.class);
        register(DataLakeDashboardResource.class);
        register(DataLakeWidgetResource.class);
        register(DataLakeResourceV3.class);
        register(PipelineElementFile.class);
        register(DashboardWidget.class);
        register(Dashboard.class);
        register(VisualizablePipelineResource.class);
        register(UserGroupResource.class);

        // Serializers
        register(JacksonSerializationProvider.class);
        register(MultiPartFeature.class);

        // Platform Services
        register(PipelineElementTemplateResource.class);
        register(DataLakeResourceV4.class);
        register(OpenApiResource.class);


        // Connect Master
        register(WelcomePageMaster.class);
        register(AdapterResource.class);
        register(DescriptionResource.class);
        register(SourcesResource.class);
        register(GuessResource.class);
//    register(MultiPartFeature.class);
        register(UnitResource.class);
        register(WorkerAdministrationResource.class);
        register(RuntimeResolvableResource.class);
    }

}
