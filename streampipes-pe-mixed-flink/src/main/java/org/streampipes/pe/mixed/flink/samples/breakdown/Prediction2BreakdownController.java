package org.streampipes.pe.mixed.flink.samples.breakdown;

import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.vocabulary.ProaSense;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;

public class Prediction2BreakdownController extends FlinkDataProcessorDeclarer<Prediction2BreakdownParameters> {

  private static final String PdfMapping = "pdf-Mapping";
  private static final String TimestampMapping = "timestamp-mapping";
  private static final String ParamsMapping = "params-mapping";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("breakdown-prediction", "Degradation Breakdown",
            "Calculates a breakdown prediction based on degradation predictions")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.domainPropertyReq
                    (ProaSense.PDFTYPE), PdfMapping, "PDF Type", "")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.listRequirement
                    (Datatypes.Long), TimestampMapping, "Timestamp Distribution Property", "")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.listRequirement
                    (Datatypes.Double), ParamsMapping, "Additional Parameter Mappings", "")
            .outputStrategy(OutputStrategies.keep())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<Prediction2BreakdownParameters> getRuntime(DataProcessorInvocation graph) {
    Prediction2BreakdownParameters params = new Prediction2BreakdownParameters(graph);

    return new Prediction2BreakdownProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
  }
}
