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

package org.apache.streampipes.sinks.databases.jvm.iotdb;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class IotDbController extends StandaloneEventSinkDeclarer<IotDbParameters> {

  private static final String DATABASE_HOST_KEY = "db_host";
  private static final String DATABASE_PORT_KEY = "db_port";
  private static final String STORAGE_GROUP_KEY = "db_storage_group";
  private static final String DATABASE_USER_KEY = "db_user";
  private static final String DATABASE_PASSWORD_KEY = "db_password";
  private static final String TIMESTAMPE_MAPPING_KEY = "timestamp_mapping";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.iotdb")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.STORAGE)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.timestampReq(),
                            Labels.withId(TIMESTAMPE_MAPPING_KEY),
                            PropertyScope.NONE)
                    .build())
            .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
            .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY), 6667)
            .requiredTextParameter(Labels.withId(STORAGE_GROUP_KEY))
            .requiredTextParameter(Labels.withId(DATABASE_USER_KEY))
            .requiredSecret(Labels.withId(DATABASE_PASSWORD_KEY))
            .build();
  }

  @Override
  public ConfiguredEventSink<IotDbParameters> onInvocation(DataSinkInvocation graph,
                                                           DataSinkParameterExtractor extractor) {

    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbStorageGroup = extractor.singleValueParameter(STORAGE_GROUP_KEY, String.class);
    String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    String password = extractor.secretValue(DATABASE_PASSWORD_KEY);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMPE_MAPPING_KEY);

    IotDbParameters params = new IotDbParameters(graph,
            hostname,
            port,
            dbStorageGroup,
            user,
            password,
            false, // SSL connection not yet implemented for IoT DB
            timestampField);

    return new ConfiguredEventSink<>(params, IotDb::new);
  }
}
