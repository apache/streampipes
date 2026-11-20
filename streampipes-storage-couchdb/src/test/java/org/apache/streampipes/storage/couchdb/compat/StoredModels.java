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

package org.apache.streampipes.storage.couchdb.compat;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.canvas.PipelineCanvasMetadata;
import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.model.client.user.RefreshToken;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.ServiceAccount;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.connect.ConnectTransformationScriptTemplate;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.model.function.FunctionState;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.storage.couchdb.serializer.GsonSerializer;

import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.function.Supplier;

/**
 * Every document type persisted through LightCouch, with the database, read type, Gson builder and view of the
 * storage in {@code impl/}. Images are attachments and covered by the client's attachment tests.
 */
final class StoredModels {

  private static final Supplier<GsonBuilder> DEFAULT = GsonSerializer::getGsonBuilder;
  private static final Supplier<GsonBuilder> ADAPTER = GsonSerializer::getAdapterGsonBuilder;
  private static final Supplier<GsonBuilder> PRINCIPAL = GsonSerializer::getPrincipalGsonBuilder;

  static final List<StoredModel> ALL = List.of(
      model("data-processor", DataProcessorDescription.class),
      model("data-sink", DataSinkDescription.class),
      model("data-stream", SpDataStream.class),
      model("pipelineelementtemplate-v2", PipelineElementTemplate.class),
      model("pipelinecanvasmetadata", PipelineCanvasMetadata.class),
      model("general-configuration", SpCoreConfiguration.class),
      model("filemetadata", FileMetadata.class),
      model("pipeline", Pipeline.class),
      model("pipeline-templates", CompactPipelineTemplate.class),
      model("dataexplorerwidget", DataExplorerWidgetModel.class),
      model("dataexplorerdashboard", DashboardModel.class),
      model("dataset", DatasetMetadata.class),
      model("extensions-services", SpServiceRegistration.class),
      model("extensions-services-configurations", SpServiceConfiguration.class),
      model("certificates", Certificate.class),
      new StoredModel("genericstorage", FunctionState.class, FunctionState.class, DEFAULT,
          "function-states/all-function-states"),
      new StoredModel("genericstorage", ConnectTransformationScriptTemplate.class,
          ConnectTransformationScriptTemplate.class, DEFAULT, "transformation-scripts/all-transformations"),
      new StoredModel("genericstorage", SpAssetModel.class, SpAssetModel.class, DEFAULT, "assets/all-assets"),
      new StoredModel("adapterinstance", AdapterDescription.class, AdapterDescription.class, ADAPTER, null),
      new StoredModel("adapterdescription", AdapterDescription.class, AdapterDescription.class, ADAPTER, null),
      new StoredModel("users", UserAccount.class, Principal.class, PRINCIPAL, "users/username"),
      new StoredModel("users", ServiceAccount.class, Principal.class, PRINCIPAL, "users/username"),
      user(Permission.class, "users/permissions"),
      user(Privilege.class, "users/privilege"),
      user(Role.class, "users/role"),
      user(Group.class, "users/groups"),
      user(UserActivationToken.class, "users/user-activation"),
      user(PasswordRecoveryToken.class, "users/password-recovery"),
      user(RefreshToken.class, "users/refresh-token-by-hash")
  );

  private StoredModels() {
  }

  private static StoredModel model(String database, Class<?> type) {
    return new StoredModel(database, type, type, DEFAULT, null);
  }

  private static StoredModel user(Class<?> type, String view) {
    return new StoredModel("users", type, type, PRINCIPAL, view);
  }
}
