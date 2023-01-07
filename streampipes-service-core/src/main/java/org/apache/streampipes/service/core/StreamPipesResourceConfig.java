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

package org.apache.streampipes.service.core;

import org.apache.streampipes.ps.DataLakeImageResource;
import org.apache.streampipes.ps.DataLakeMeasureResourceV3;
import org.apache.streampipes.ps.DataLakeMeasureResourceV4;
import org.apache.streampipes.ps.DataLakeResourceV3;
import org.apache.streampipes.ps.DataLakeResourceV4;
import org.apache.streampipes.ps.PipelineElementTemplateResource;
import org.apache.streampipes.rest.impl.AccountActivationResource;
import org.apache.streampipes.rest.impl.AdapterMonitoringResource;
import org.apache.streampipes.rest.impl.AssetDashboardResource;
import org.apache.streampipes.rest.impl.AssetManagementResource;
import org.apache.streampipes.rest.impl.Authentication;
import org.apache.streampipes.rest.impl.AutoComplete;
import org.apache.streampipes.rest.impl.CategoryResource;
import org.apache.streampipes.rest.impl.ContainerProvidedOptions;
import org.apache.streampipes.rest.impl.DataStream;
import org.apache.streampipes.rest.impl.EmailResource;
import org.apache.streampipes.rest.impl.FunctionsResource;
import org.apache.streampipes.rest.impl.GenericStorageResource;
import org.apache.streampipes.rest.impl.LabelResource;
import org.apache.streampipes.rest.impl.MeasurementUnitResource;
import org.apache.streampipes.rest.impl.Notification;
import org.apache.streampipes.rest.impl.OntologyMeasurementUnit;
import org.apache.streampipes.rest.impl.PipelineCache;
import org.apache.streampipes.rest.impl.PipelineCanvasMetadataCache;
import org.apache.streampipes.rest.impl.PipelineCanvasMetadataResource;
import org.apache.streampipes.rest.impl.PipelineCategory;
import org.apache.streampipes.rest.impl.PipelineElementAsset;
import org.apache.streampipes.rest.impl.PipelineElementCategory;
import org.apache.streampipes.rest.impl.PipelineElementFile;
import org.apache.streampipes.rest.impl.PipelineElementPreview;
import org.apache.streampipes.rest.impl.PipelineElementRuntimeInfo;
import org.apache.streampipes.rest.impl.PipelineMonitoring;
import org.apache.streampipes.rest.impl.PipelineResource;
import org.apache.streampipes.rest.impl.PipelineTemplate;
import org.apache.streampipes.rest.impl.ResetResource;
import org.apache.streampipes.rest.impl.RestorePasswordResource;
import org.apache.streampipes.rest.impl.Setup;
import org.apache.streampipes.rest.impl.UserResource;
import org.apache.streampipes.rest.impl.Version;
import org.apache.streampipes.rest.impl.admin.ConsulConfig;
import org.apache.streampipes.rest.impl.admin.DataExportResource;
import org.apache.streampipes.rest.impl.admin.DataImportResource;
import org.apache.streampipes.rest.impl.admin.EmailConfigurationResource;
import org.apache.streampipes.rest.impl.admin.ExtensionsServiceEndpointResource;
import org.apache.streampipes.rest.impl.admin.GeneralConfigurationResource;
import org.apache.streampipes.rest.impl.admin.PermissionResource;
import org.apache.streampipes.rest.impl.admin.PipelineElementImport;
import org.apache.streampipes.rest.impl.admin.UserGroupResource;
import org.apache.streampipes.rest.impl.connect.AdapterResource;
import org.apache.streampipes.rest.impl.connect.DescriptionResource;
import org.apache.streampipes.rest.impl.connect.GuessResource;
import org.apache.streampipes.rest.impl.connect.RuntimeResolvableResource;
import org.apache.streampipes.rest.impl.connect.SourcesResource;
import org.apache.streampipes.rest.impl.connect.UnitResource;
import org.apache.streampipes.rest.impl.connect.WorkerAdministrationResource;
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

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.ApplicationPath;

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
