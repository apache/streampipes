package de.fzi.cep.sepa.flink.samples.enrich.timestamp;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

import java.util.ArrayList;
import java.util.List;

public class TimestampController extends AbstractFlinkAgentDeclarer<TimestampParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
            "Appends the current time in ms to the event payload using Flink")
            .iconUrl(Config.getIconUrl("enrich-timestamp-icon"))
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
    System.out.println(Config.JAR_FILE);
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

    return new TimestampProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
//		return new TimestampProgram(staticParam);
  }

}
