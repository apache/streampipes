package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.routing.EventProcessorInputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class FlatSpInputCollector<T extends TransportProtocol> extends
        FlatSpCollector<T, EventProcessor<?>>
        implements
        InternalEventProcessor<byte[]>, EventProcessorInputCollector {

  private Boolean singletonEngine;


  public FlatSpInputCollector(T protocol, TransportFormat format,
                              Boolean singletonEngine) throws SpRuntimeException {
    super(protocol, format);
    this.singletonEngine = singletonEngine;
  }

  @Override
  public void onEvent(byte[] event) {
    if (singletonEngine) {
     send(consumers.get(consumers.keySet().toArray()[0]), event);
    } else {
      consumers.keySet().forEach(c -> {
        send(consumers.get(c), event);
      });
    }
  }

  private void send(EventProcessor<?> processor, byte[] event) {
    try {
      processor.onEvent(dataFormatDefinition.toMap(event), getTopic());
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void connect() throws SpRuntimeException {
    if (!protocolDefinition.getConsumer().isConnected()) {
      protocolDefinition.getConsumer().connect
              (transportProtocol,
                      this);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (protocolDefinition.getConsumer().isConnected()) {
      if (consumers.size() == 0) {
        protocolDefinition.getConsumer().disconnect();
      }
    }
  }
}
