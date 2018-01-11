package org.streampipes.pe.mixed.flink.samples.elasticsearch;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
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

public class ElasticSearchController extends FlinkDataSinkDeclarer<ElasticSearchParameters> {

	@Override
	public DataSinkDescription declareModel() {
		return DataSinkBuilder.create("elasticsearch", "Elasticsearch", "Stores data in an elasticsearch cluster")
						.category(DataSinkType.STORAGE)
						.iconUrl(FlinkConfig.getIconUrl("elasticsearch_icon"))
						.requiredStream(StreamRequirementsBuilder
										.create()
										.requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(), Labels.from("timestamp",
														"Timestamp Property", "Timestamp Mapping"), PropertyScope.HEADER_PROPERTY)
										.build())
						.requiredTextParameter("index-name", "Index Name", "Elasticsearch index name property")
						.supportedFormats(SupportedFormats.jsonFormat())
						.supportedProtocols(SupportedProtocols.kafka())
						.build();
	}

	@Override
	public FlinkDataSinkRuntime getRuntime(DataSinkInvocation graph) {
		DataSinkParameterExtractor extractor = DataSinkParameterExtractor.from(graph);

		String timestampField = extractor.mappingPropertyValue("timestamp");
		String indexName = extractor.singleValueParameter("index-name", String.class);

		ElasticSearchParameters params = new ElasticSearchParameters(graph, timestampField, indexName);

		return new ElasticSearchProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
						FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//		return new ElasticSearchProgram(params);
	}

}
