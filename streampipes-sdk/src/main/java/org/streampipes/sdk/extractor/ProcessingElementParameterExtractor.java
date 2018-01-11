package org.streampipes.sdk.extractor;

import org.streampipes.model.graph.DataProcessorInvocation;

public class ProcessingElementParameterExtractor extends AbstractParameterExtractor<DataProcessorInvocation> {

  public static ProcessingElementParameterExtractor from(DataProcessorInvocation sepaElement) {
    return new ProcessingElementParameterExtractor(sepaElement);
  }

  public ProcessingElementParameterExtractor(DataProcessorInvocation sepaElement) {
    super(sepaElement);
  }

  public String outputTopic() {
    return sepaElement
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicName();
  }

}
