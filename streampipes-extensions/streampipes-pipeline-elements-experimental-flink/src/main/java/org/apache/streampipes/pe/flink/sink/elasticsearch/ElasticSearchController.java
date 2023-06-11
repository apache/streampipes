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

package org.apache.streampipes.pe.flink.sink.elasticsearch;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataSinkProgram;

public class ElasticSearchController extends FlinkDataSinkDeclarer<ElasticSearchParameters> {

  private static final String INDEX_NAME = "index-name";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.databases.flink.elasticsearch")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.STORAGE)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(TIMESTAMP_MAPPING), PropertyScope.HEADER_PROPERTY)
            .build())
        .requiredTextParameter(Labels.withId(INDEX_NAME))
        .build();
  }

  @Override
  public FlinkDataSinkProgram<ElasticSearchParameters> getProgram(DataSinkInvocation graph,
                                                                  IDataSinkParameterExtractor extractor) {

    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);
    String indexName = extractor.singleValueParameter(INDEX_NAME, String.class);
    //TODO after refactoring
    // String elasticsearchHost = configExtractor.getConfig().getString(ConfigKeys.ELASTIC_HOST);
    //Integer elasticsearchPort = configExtractor.getConfig().getInteger(ConfigKeys.ELASTIC_PORT_REST);

    ElasticSearchParameters params =
        new ElasticSearchParameters(graph, timestampField, indexName, null, null);

    return new ElasticSearchProgram(params);

  }

}
