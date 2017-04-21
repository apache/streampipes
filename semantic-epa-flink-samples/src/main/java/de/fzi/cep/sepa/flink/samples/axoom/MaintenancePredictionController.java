package de.fzi.cep.sepa.flink.samples.axoom;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
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
            "Prediction (Rule-based)", "Predicts the next maintenance based on coffee orders")
            .category(EpaType.ALGORITHM)
            .iconUrl(Config.getIconUrl("prediction-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .requiredPropertyStream2(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.fixed(EpProperties.longEp("timestamp", SO.DateTime)
                    , EpProperties.stringEp("machineId", "http://axoom.com/machineId"),
                    EpProperties.longEp("predictedMaintenanceTime", SO.DateTime)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<MaintenancePredictionParameters> getRuntime(SepaInvocation graph) {
    MaintenancePredictionParameters params = new MaintenancePredictionParameters(graph);

    return new MaintenancePredictionProgram(params, new FlinkDeploymentConfig(Config.JAR_FILE,
            Config.FLINK_HOST, Config.FLINK_PORT));
  }
}
