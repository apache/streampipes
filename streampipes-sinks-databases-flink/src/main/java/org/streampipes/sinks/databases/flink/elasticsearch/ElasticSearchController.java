/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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

package org.streampipes.sinks.databases.flink.elasticsearch;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sinks.databases.flink.config.DatabasesFlinkConfig;
import org.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;

public class ElasticSearchController extends FlinkDataSinkDeclarer<ElasticSearchParameters> {

  private static final String INDEX_NAME = "index-name";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.streampipes.sinks.databases.flink.elasticsearch", "Elasticsearch", "Stores data in an elasticsearch cluster")
            .category(DataSinkType.STORAGE)
            .iconUrl(DatabasesFlinkConfig.getIconUrl("elasticsearch_icon"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(), Labels.from(TIMESTAMP_MAPPING,
                            "Timestamp Property", "Timestamp Mapping"), PropertyScope.HEADER_PROPERTY)
                    .build())
            .requiredTextParameter(Labels.from(INDEX_NAME, "Index Name", "Elasticsearch index name property"))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataSinkRuntime<ElasticSearchParameters> getRuntime(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);
    String indexName = extractor.singleValueParameter(INDEX_NAME, String.class);

    ElasticSearchParameters params = new ElasticSearchParameters(graph, timestampField, indexName);

    return new ElasticSearchProgram(params, DatabasesFlinkConfig.INSTANCE.getDebug());

  }

}
