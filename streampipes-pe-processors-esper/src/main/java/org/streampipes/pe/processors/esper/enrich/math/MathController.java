package org.streampipes.pe.processors.esper.enrich.math;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.List;

public class MathController extends StandaloneEventProcessorDeclarerSingleton<MathParameter> {

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("math", "Math EPA",
            "performs simple calculations on event properties")
            .iconUrl(EsperConfig.getIconUrl("math-icon"))
            .category(DataProcessorType.ALGORITHM)
            .requiredPropertyStream1WithNaryMapping(EpRequirements.numberReq(), "leftOperand", "Select left operand", "")
            .requiredPropertyStream1WithNaryMapping(EpRequirements.numberReq(), "rightOperand", "Select right operand", "")
            .outputStrategy(OutputStrategies.append(EpProperties.longEp(Labels.empty(), "result", SO.Number)))
            .requiredSingleValueSelection("operation", "Select Operation", "", Options.from("+", "-", "/", "*"))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<MathParameter> onInvocation(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {

    String operation = extractor.selectedSingleValue("operation", String.class);
    String leftOperand = extractor.mappingPropertyValue("leftOperand");
    String rightOperand = extractor.mappingPropertyValue("rightOperand");

    AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

    String appendPropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "result");

    Operation arithmeticOperation;
    if (operation.equals("+")) {
      arithmeticOperation = Operation.ADD;
    } else if (operation.equals("-")) {
      arithmeticOperation = Operation.SUBTRACT;
    } else if (operation.equals("*")) {
      arithmeticOperation = Operation.MULTIPLY;
    } else {
      arithmeticOperation = Operation.DIVIDE;
    }

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    MathParameter staticParam = new MathParameter(sepa,
            selectProperties,
            arithmeticOperation,
            leftOperand,
            rightOperand,
            appendPropertyName);

    return new ConfiguredEventProcessor<>(staticParam, Math::new);
  }
}
