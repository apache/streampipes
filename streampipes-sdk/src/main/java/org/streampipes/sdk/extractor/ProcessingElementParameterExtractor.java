package org.streampipes.sdk.extractor;

import org.streampipes.model.impl.graph.SepaInvocation;

/**
 * Created by riemer on 20.03.2017.
 */
public class ProcessingElementParameterExtractor extends AbstractParameterExtractor<SepaInvocation> {

  public static ProcessingElementParameterExtractor from(SepaInvocation sepaElement) {
    return new ProcessingElementParameterExtractor(sepaElement);
  }

  public ProcessingElementParameterExtractor(SepaInvocation sepaElement) {
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
