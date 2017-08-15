package org.streampipes.wrapper.standalone.routing;

import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.routing.PipelineElementCollector;

import java.util.HashMap;
import java.util.Map;

public abstract class FlatSpCollector<T extends TransportProtocol, C> implements
        PipelineElementCollector<C> {

  protected Map<String, C> consumers;

  protected T transportProtocol;
  protected TransportFormat transportFormat;


  public FlatSpCollector(T protocol, TransportFormat format) {
    this.transportProtocol = protocol;
    this.transportFormat = format;
    this.consumers = new HashMap<>();
  }

  public void registerConsumer(String routeId, C consumer) {
    consumers.put(routeId, consumer);
  }

  public void unregisterConsumer(String routeId) {
    consumers.remove(routeId);
  }

}
