package de.fzi.cep.sepa.flink.samples.breakdown;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.ProaSense;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;
import de.fzi.cep.sepa.sdk.utils.Datatypes;

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

    return new Prediction2BreakdownProgram(params, new FlinkDeploymentConfig(Config.JAR_FILE,
            Config.FLINK_HOST, Config
            .FLINK_PORT));
  }
}
