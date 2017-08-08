package org.streampipes.wrapper.flink.samples.breakdown;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.vocabulary.ProaSense;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownController extends AbstractFlinkAgentDeclarer<Prediction2BreakdownParameters> {

  private static final String PdfMapping = "pdf-Mapping";
  private static final String TimestampMapping = "timestamp-mapping";
  private static final String ParamsMapping = "params-mapping";

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("breakdown-prediction", "Degradation Breakdown",
            "Calculates a breakdown prediction based on degradation predictions")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.domainPropertyReq
                    (ProaSense.PDFTYPE), PdfMapping, "PDF Type", "")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.listRequirement
                    (Datatypes.Long), TimestampMapping, "Timestamp Distribution Property", "")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.listRequirement
                    (Datatypes.Double), ParamsMapping, "Additional Parameter Mappings", "")
            .outputStrategy(OutputStrategies.keep())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<Prediction2BreakdownParameters> getRuntime(SepaInvocation graph) {
    Prediction2BreakdownParameters params = new Prediction2BreakdownParameters(graph);

    return new Prediction2BreakdownProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.FLINK_HOST, FlinkConfig
            .FLINK_PORT));
  }
}
