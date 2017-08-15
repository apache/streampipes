package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;

import java.util.Map;

public class FlatSpOutputCollector<T extends TransportProtocol> extends
        FlatSpCollector<InternalEventProcessor<Map<String,
        Object>>> implements
        InternalEventProcessor<Map<String,
        Object>>, EventProcessorOutputCollector {

  private EventProducer<?> eventProducer;

  public FlatSpOutputCollector(T protocol, TransportFormat format) {
   super(protocol, format);
    this.eventProducer = producer;
  }

  public void onEvent(Map<String, Object> outEvent) {
    try {
      eventProducer.publish(dataFormatDefinition.fromMap(outEvent));
    } catch (SpRuntimeException e) {
      // TODO handle exception
      e.printStackTrace();
    }
  }


}
