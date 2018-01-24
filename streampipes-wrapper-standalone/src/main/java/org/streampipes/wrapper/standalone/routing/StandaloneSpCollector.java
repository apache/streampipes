package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.routing.PipelineElementCollector;
import org.streampipes.wrapper.standalone.manager.PManager;

import java.util.HashMap;
import java.util.Map;

public abstract class StandaloneSpCollector<T extends TransportProtocol, C> implements
        PipelineElementCollector<C> {

  protected Map<String, C> consumers;

  protected T transportProtocol;
  protected SpProtocolDefinition<T> protocolDefinition;

  protected TransportFormat transportFormat;
  protected SpDataFormatDefinition dataFormatDefinition;


  public StandaloneSpCollector(T protocol, TransportFormat format) throws SpRuntimeException {
    this.transportProtocol = protocol;
    this.protocolDefinition = PManager.getProtocolDefinition(protocol).orElseThrow(() -> new
            SpRuntimeException("Could not find protocol"));
    this.transportFormat = format;
    this.dataFormatDefinition = PManager.getDataFormat(format).orElseThrow(() -> new
            SpRuntimeException("Could not find format"));
    this.consumers = new HashMap<>();
  }

  public void registerConsumer(String routeId, C consumer) {
    consumers.put(routeId, consumer);
  }

  public void unregisterConsumer(String routeId) {
    consumers.remove(routeId);
  }

  protected String getTopic() {
    return transportProtocol.getTopicDefinition().getActualTopicName();
  }

}
