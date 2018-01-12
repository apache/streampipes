package org.streampipes.pe.mixed.flink.samples.axoom;

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
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

public class MaintenancePredictionController extends FlinkDataProcessorDeclarer<MaintenancePredictionParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("maintenance-prediction", "Coffee Maintenance " +
            "Prediction (Rule-based)", "Predicts the next maintenance based on coffee orders")
            .category(DataProcessorType.ALGORITHM)
            .iconUrl(FlinkConfig.getIconUrl("prediction-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .requiredPropertyStream2(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.fixed(EpProperties.longEp(Labels.empty(), "timestamp", SO.DateTime)
                    , EpProperties.stringEp(Labels.empty(), "machineId", "http://axoom.com/machineId"),
                    EpProperties.longEp(Labels.empty(), "predictedMaintenanceTime", SO.DateTime)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<MaintenancePredictionParameters> getRuntime(DataProcessorInvocation graph) {
    MaintenancePredictionParameters params = new MaintenancePredictionParameters(graph);

    return new MaintenancePredictionProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

    //return new MaintenancePredictionProgram(params);
  }
}
