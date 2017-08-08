package org.streampipes.pe.mixed.flink.samples.enrich.timestamp;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

import java.util.ArrayList;
import java.util.List;

public class TimestampController extends AbstractFlinkAgentDeclarer<TimestampParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
            "Appends the current time in ms to the event payload using Flink")
            .iconUrl(FlinkConfig.getIconUrl("enrich-timestamp-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp("appendedTime", SO.DateTime)))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<TimestampParameters> getRuntime(
          SepaInvocation graph) {
    System.out.println(FlinkConfig.JAR_FILE);
    AppendOutputStrategy strategy = (AppendOutputStrategy) graph.getOutputStrategies().get(0);

    String appendTimePropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "appendedTime");

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    TimestampParameters staticParam = new TimestampParameters(
            graph,
            appendTimePropertyName,
            selectProperties);

    return new TimestampProgram(staticParam, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//		return new TimestampProgram(staticParam);
  }

}
