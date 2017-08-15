package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.routing.EventProcessorInputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.Optional;

public class FlatSpInputCollector<T extends TransportProtocol> extends
        FlatSpCollector<T, EventProcessor<?>>
        implements
        InternalEventProcessor<byte[]>, EventProcessorInputCollector {

  private EventConsumer<?> consumer;
  private Boolean singletonEngine;

  private Optional<SpProtocolDefinition<T>> protocolDefinition;


  public FlatSpInputCollector(T protocol, TransportFormat format,
                              Boolean singletonEngine) {
    super(protocol, format);
    this.consumer = consumer;
    this.singletonEngine = singletonEngine;
    this.protocolDefinition = PManager.get

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
      processor.onEvent(dataFormatDefinition.toMap(event), topic);
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void connect() throws SpRuntimeException {

  }

  @Override
  public void disconnect() throws SpRuntimeException {

  }
}
