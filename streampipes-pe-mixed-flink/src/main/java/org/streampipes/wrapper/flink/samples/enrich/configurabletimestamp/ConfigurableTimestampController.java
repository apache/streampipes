package org.streampipes.wrapper.flink.samples.enrich.configurabletimestamp;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

public class ConfigurableTimestampController extends AbstractFlinkAgentDeclarer<ConfigurableTimestampParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
            "Appends the current time in ms to the event payload using Flink")
            .category(EpaType.ENRICH)
            .iconUrl(FlinkConfig.getIconUrl("enrich-timestamp-icon"))
            .requiredTextParameter("timestamp_name", "Timestamp Name", "The label that is used for the appended timestamp")
            .requiredIntegerParameter("blk", "sdf", "asdf")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp("appendedTime", SO.DateTime)))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<ConfigurableTimestampParameters> getRuntime(
          SepaInvocation graph) {

    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(graph);

    String timestampName = extractor.mappingPropertyValue("timestamp_name");

    ConfigurableTimestampParameters staticParam = new ConfigurableTimestampParameters(
            graph,
            timestampName
    );

    return new ConfigurableTimestampProgram(staticParam, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));
//		return new ConfigurableTimestampProgram(staticParam);
  }

}
