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

package org.apache.streampipes.sinks.internal.jvm.datalake;

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
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

import java.util.List;

public class DataLakeController extends StandaloneEventSinkDeclarer<DataLakeParameters> {

  private static final String DATABASE_MEASUREMENT_KEY = "db_measurement";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.internal.jvm.datalake")
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
    measureName = DataLake.prepareString(measureName);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_KEY);

    String hostname = SinksInternalJvmConfig.INSTANCE.getDataLakeProtocol() + "://" + SinksInternalJvmConfig.INSTANCE.getDataLakeHost();
    Integer port = SinksInternalJvmConfig.INSTANCE.getDataLakePort();
    String dbName = SinksInternalJvmConfig.INSTANCE.getDataLakeDatabaseName();
    String user = SinksInternalJvmConfig.INSTANCE.getDataLakeUsername();
    String password = SinksInternalJvmConfig.INSTANCE.getDataLakePassword();
    Integer batch_size = 2000;
    Integer flush_duration = 500;

    List<String> dimensionProperties = extractor.getEventPropertiesRuntimeNamesByScope(PropertyScope.DIMENSION_PROPERTY);

    DataLakeParameters params = new DataLakeParameters(graph,
            hostname,
            port,
            dbName,
            measureName,
            user,
            password,
            timestampField,
            batch_size,
            flush_duration,
            dimensionProperties);


    return new ConfiguredEventSink<>(params, DataLake::new);
  }
}
