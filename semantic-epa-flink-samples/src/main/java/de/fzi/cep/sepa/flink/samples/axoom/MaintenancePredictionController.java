package de.fzi.cep.sepa.flink.samples.axoom;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionController extends AbstractFlinkAgentDeclarer<MaintenancePredictionParameters> {

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("maintenance-prediction", "Coffee Maintenance " +
            "Prediction", "Predicts the next maintenance based on coffee orders")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .requiredPropertyStream2(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.keep(false))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<MaintenancePredictionParameters> getRuntime(SepaInvocation graph) {
    MaintenancePredictionParameters params = new MaintenancePredictionParameters(graph);

    return new MaintenancePredictionProgram(params);
  }
}
