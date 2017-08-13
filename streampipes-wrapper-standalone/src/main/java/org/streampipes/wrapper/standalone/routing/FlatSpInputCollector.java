package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.impl.TransportProtocol;

public class FlatSpInputCollector extends FlatSpCollector implements InternalEventProcessor<byte[]> {

  private EventConsumer<TransportProtocol> consumer;

  public FlatSpInputCollector(SpDataFormatDefinition dataFormatDefinition, EventConsumer<TransportProtocol> consumer) {
    super(dataFormatDefinition);
    this.consumer = consumer;
  }

  @Override
  protected void connect(TransportProtocol protocol) throws SpRuntimeException {
    consumer.connect(protocol, this);
  }

  @Override
  protected void disconnect() throws SpRuntimeException {
    consumer.disconnect();
  }

  @Override
  public void onEvent(byte[] event) {
    consumers.keySet().forEach(c -> {
      try {
        consumers.get(c).onEvent(dataFormatDefinition.toMap(event));
      } catch (SpRuntimeException e) {
        e.printStackTrace();
      }
    });
  }
}
