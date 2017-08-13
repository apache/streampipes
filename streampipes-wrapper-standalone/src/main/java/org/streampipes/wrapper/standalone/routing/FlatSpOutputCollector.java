package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.impl.TransportProtocol;

import java.util.Map;

public class FlatSpOutputCollector extends FlatSpCollector implements
        InternalEventProcessor<Map<String,
        Object>> {

  private EventProducer<?> eventProducer;

  public FlatSpOutputCollector(SpDataFormatDefinition dataFormatDefinition, EventProducer<?>
          producer) {
   super(dataFormatDefinition);
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

  @Override
  protected void connect(TransportProtocol protocol) throws SpRuntimeException {

  }

  @Override
  protected void disconnect() throws SpRuntimeException {

  }
}
