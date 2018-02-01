package org.streampipes.pe.mixed.flink.samples.enrich.configurabletimestamp;

import org.streampipes.sdk.helpers.Labels;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.vocabulary.SO;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

public class ConfigurableTimestampController extends FlinkDataProcessorDeclarer<ConfigurableTimestampParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
            "Appends the current time in ms to the event payload using Flink")
            .category(DataProcessorType.ENRICH)
            .iconUrl(FlinkConfig.getIconUrl("enrich-timestamp-icon"))
            .requiredTextParameter("timestamp_name", "Timestamp Name", "The label that is used for the appended timestamp")
            .requiredIntegerParameter("blk", "sdf", "asdf")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp(Labels.empty(), "appendedTime", SO.DateTime)))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<ConfigurableTimestampParameters> getRuntime(
          DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String timestampName = extractor.mappingPropertyValue("timestamp_name");

    ConfigurableTimestampParameters staticParam = new ConfigurableTimestampParameters(
            graph,
            timestampName
    );

    return new ConfigurableTimestampProgram(staticParam, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//		return new ConfigurableTimestampProgram(staticParam);
  }

}
