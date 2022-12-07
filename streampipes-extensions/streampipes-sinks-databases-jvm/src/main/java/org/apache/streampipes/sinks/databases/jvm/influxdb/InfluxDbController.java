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

package org.apache.streampipes.sinks.databases.jvm.influxdb;

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

public class InfluxDbController extends StandaloneEventSinkDeclarer<InfluxDbParameters> {

  private static final String DATABASE_HOST_KEY = "db_host";
  private static final String DATABASE_PORT_KEY = "db_port";
  private static final String DATABASE_NAME_KEY = "db_name";
  private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
  private static final String DATABASE_USER_KEY = "db_user";
  private static final String DATABASE_PASSWORD_KEY = "db_password";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";
  private static final String BATCH_INTERVAL_ACTIONS_KEY = "batch_interval_actions";
  private static final String MAX_FLUSH_DURATION_KEY = "max_flush_duration";


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.jvm.influxdb")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.DATABASE)
        .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
            EpRequirements.timestampReq(),
            Labels.withId(TIMESTAMP_MAPPING_KEY),
            PropertyScope.NONE).build())
        .requiredTextParameter(Labels.withId(DATABASE_HOST_KEY))
        .requiredIntegerParameter(Labels.withId(DATABASE_PORT_KEY), 8086)
        .requiredIntegerParameter(Labels.withId(BATCH_INTERVAL_ACTIONS_KEY))
        .requiredIntegerParameter(Labels.withId(MAX_FLUSH_DURATION_KEY), 2000)
        .requiredTextParameter(Labels.withId(DATABASE_NAME_KEY))
        .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
        .requiredTextParameter(Labels.withId(DATABASE_USER_KEY))
        .requiredSecret(Labels.withId(DATABASE_PASSWORD_KEY))
        .build();
  }

  @Override
  public ConfiguredEventSink<InfluxDbParameters> onInvocation(DataSinkInvocation graph,
                                                              DataSinkParameterExtractor extractor) {

    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbName = extractor.singleValueParameter(DATABASE_NAME_KEY, String.class);
    String measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    measureName = InfluxDb.prepareString(measureName);
    String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    String password = extractor.secretValue(DATABASE_PASSWORD_KEY);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    Integer batchSize = extractor.singleValueParameter(BATCH_INTERVAL_ACTIONS_KEY, Integer.class);
    Integer flushDuration = extractor.singleValueParameter(MAX_FLUSH_DURATION_KEY, Integer.class);

    InfluxDbParameters params = new InfluxDbParameters(graph,
        hostname,
        port,
        dbName,
        measureName,
        user,
        password,
        timestampField,
        batchSize,
        flushDuration);

    return new ConfiguredEventSink<>(params, InfluxDb::new);
  }
}
