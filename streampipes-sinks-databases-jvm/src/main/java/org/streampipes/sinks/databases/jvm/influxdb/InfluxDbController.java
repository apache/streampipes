/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sinks.databases.jvm.influxdb;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

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
    return DataSinkBuilder.create("org.streampipes.sinks.databases.jvm.influxdb")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.STORAGE)
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
            .requiredTextParameter(Labels.withId(DATABASE_PASSWORD_KEY))
            .build();
  }

  @Override
  public ConfiguredEventSink<InfluxDbParameters> onInvocation(DataSinkInvocation graph,
                                                              DataSinkParameterExtractor extractor) {

    String hostname = extractor.singleValueParameter(DATABASE_HOST_KEY, String.class);
    Integer port = extractor.singleValueParameter(DATABASE_PORT_KEY, Integer.class);
    String dbName = extractor.singleValueParameter(DATABASE_NAME_KEY, String.class);
    String measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    String user = extractor.singleValueParameter(DATABASE_USER_KEY, String.class);
    String password = extractor.singleValueParameter(DATABASE_PASSWORD_KEY, String.class);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    Integer batch_size = extractor.singleValueParameter(BATCH_INTERVAL_ACTIONS_KEY, Integer.class);
    Integer flush_duration = extractor.singleValueParameter(MAX_FLUSH_DURATION_KEY, Integer.class);

    InfluxDbParameters params = new InfluxDbParameters(graph,
            hostname,
            port,
            dbName,
            measureName,
            user,
            password,
            timestampField,
            batch_size,
            flush_duration);

    return new ConfiguredEventSink<>(params, InfluxDb::new);
  }
}
