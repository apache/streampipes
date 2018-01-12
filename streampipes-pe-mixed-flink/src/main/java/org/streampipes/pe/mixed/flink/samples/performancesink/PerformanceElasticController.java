/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.mixed.flink.samples.performancesink;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.pe.mixed.flink.samples.elasticsearch.ElasticSearchParameters;
import org.streampipes.pe.mixed.flink.samples.elasticsearch.ElasticSearchProgram;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

public class PerformanceElasticController extends FlinkDataSinkDeclarer<ElasticSearchParameters> {

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("performance-elasticsearch", "Elasticsearch Performance Test Sink", "Stores data" +
            " in an elasticsearch cluster")
            .category(DataSinkType.STORAGE)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(), Labels.from("timestamp",
                            "Timestamp Property", "Timestamp Mapping"), PropertyScope.HEADER_PROPERTY)
                    .build())
            .requiredTextParameter("index-name", "Index Name", "Elasticsearch index name property")
            .requiredIntegerParameter(Labels.from("epa-count", "EPA count", ""))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataSinkRuntime getRuntime(DataSinkInvocation graph) {
    DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(graph);

    String timestampField = extractor.mappingPropertyValue("timestamp");
    String indexName = extractor.singleValueParameter("index-name", String.class);
    Integer epaCount = extractor.singleValueParameter("epa-count", Integer.class);

    PerformanceElasticParameters params = new PerformanceElasticParameters(graph, timestampField, indexName, epaCount);

    return new PerformanceElasticProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//		return new ElasticSearchProgram(params);
  }

}

