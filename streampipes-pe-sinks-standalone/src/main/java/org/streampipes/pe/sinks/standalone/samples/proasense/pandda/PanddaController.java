package org.streampipes.pe.sinks.standalone.samples.proasense.pandda;

import org.streampipes.pe.sinks.standalone.samples.NonVisualizableActionController;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.ProaSense;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;

/**
 * Created by riemer on 12.02.2017.
 */
public class PanddaController extends NonVisualizableActionController {

  private static final String PdfMapping = "pdf-Mapping";
  private static final String TimestampMapping = "timestamp-mapping";
  private static final String ParamsMapping = "params-mapping";

  private PanddaPublisher panddaPublisher;

  @Override
  public SecDescription declareModel() {
      return DataSinkBuilder.create("pandda", "PANDDA", "Forwards an event" +
              " to the ProaSense PANDDA component")
              .stream1PropertyRequirementWithUnaryMapping(EpRequirements.domainPropertyReq
                      (ProaSense.PDFTYPE), PdfMapping, "PDF Type", "")
              .stream1PropertyRequirementWithUnaryMapping(EpRequirements.listRequirement
                      (Datatypes.Long), TimestampMapping, "Timestamp Distribution Property", "")
              .stream1PropertyRequirementWithUnaryMapping(EpRequirements.listRequirement
                      (Datatypes.Double), ParamsMapping, "Additional Parameter Mappings", "")
              .supportedProtocols(SupportedProtocols.kafka())
              .supportedFormats(SupportedFormats.jsonFormat())
              .build();
  }


  @Override
  public Response invokeRuntime(SecInvocation sec) {
    String consumerTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();


    PanddaParameters panddaParams = new PanddaParameters();
    panddaParams.setPdfTypePropertyKey(SepaUtils.getMappingPropertyName(sec, PdfMapping));
    panddaParams.setParamsPropertyKey(SepaUtils.getMappingPropertyName(sec, TimestampMapping));
    panddaParams.setParamsPropertyKey(SepaUtils.getMappingPropertyName(sec, ParamsMapping));

    panddaPublisher = new PanddaPublisher(ClientConfiguration.INSTANCE.getKafkaHost(),
            ClientConfiguration.INSTANCE.getKafkaPort(), panddaParams);
    startKafkaConsumer(ClientConfiguration.INSTANCE.getKafkaUrl(), consumerTopic,
            panddaPublisher);

    String pipelineId = sec.getCorrespondingPipeline();
    return new Response(pipelineId, true);

  }

  @Override
  public Response detachRuntime(String pipelineId) {
    stopKafkaConsumer();
    panddaPublisher.closePublisher();
    return new Response(pipelineId, true);
  }
}
