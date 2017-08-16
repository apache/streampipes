package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.Map;

public class FlatSpOutputCollector<T extends TransportProtocol> extends
        FlatSpCollector<T, InternalEventProcessor<Map<String,
        Object>>> implements EventProcessorOutputCollector {


  public FlatSpOutputCollector(T protocol, TransportFormat format) throws SpRuntimeException {
   super(protocol, format);
  }

  public void onEvent(Map<String, Object> outEvent) {
    try {
      protocolDefinition.getProducer().publish(dataFormatDefinition.fromMap(outEvent));
    } catch (SpRuntimeException e) {
      // TODO handle exception
      e.printStackTrace();
    }
  }


  @Override
  public void connect() throws SpRuntimeException {
    if (!protocolDefinition.getProducer().isConnected()) {
      protocolDefinition.getProducer().connect(transportProtocol);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (protocolDefinition.getProducer().isConnected()) {
      protocolDefinition.getProducer().disconnect();
      ProtocolManager.removeOutputCollector(transportProtocol);
    }
  }
}
