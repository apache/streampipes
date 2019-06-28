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
 *
 */

package org.streampipes.sinks.internal.jvm.datalake;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.datalake.DataLakeMeasure;
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
import org.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class DataLakeController extends StandaloneEventSinkDeclarer<DataLakeParameters> {

  private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.streampipes.sinks.internal.jvm.datalake")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .category(DataSinkType.STORAGE)
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
                    EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_MAPPING_KEY),
                    PropertyScope.NONE).build())
            .requiredTextParameter(Labels.withId(DATABASE_MEASUREMENT_KEY))
            .build();
  }

  @Override
  public ConfiguredEventSink<DataLakeParameters> onInvocation(DataSinkInvocation graph,
                                                              DataSinkParameterExtractor extractor) {


    String measureName = extractor.singleValueParameter(DATABASE_MEASUREMENT_KEY, String.class);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);

    String hostname = SinksInternalJvmConfig.INSTANCE.getDataLakeProtocol() + "://" + SinksInternalJvmConfig.INSTANCE.getDataLakeHost();
    Integer port = SinksInternalJvmConfig.INSTANCE.getDataLakePort();
    String dbName = "sp";
    String user = "default";
    String password = "default";
    Integer batch_size = 100;
    Integer flush_duration = 1000;

    DataLakeParameters params = new DataLakeParameters(graph,
            hostname,
            port,
            dbName,
            measureName,
            user,
            password,
            timestampField,
            batch_size,
            flush_duration);


    return new ConfiguredEventSink<>(params, DataLake::new);
  }
}
