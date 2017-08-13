package org.streampipes.wrapper.standalone.routing;

import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.wrapper.runtime.SpCollector;

import java.util.HashMap;
import java.util.Map;

public abstract class FlatSpCollector implements SpCollector<Map<S>> {

  protected Map<String, InternalEventProcessor<Map<String, Object>>> consumers;
  protected SpDataFormatDefinition dataFormatDefinition;

  public FlatSpCollector(SpDataFormatDefinition dataFormatDefinition) {
    this.dataFormatDefinition = dataFormatDefinition;
    this.consumers = new HashMap<>();

  }

  public void registerConsumer(String routeId, InternalEventProcessor<Map<String, Object>>
          consumer) {
    consumers.put(routeId, consumer);
  }

  public void unregisterConsumer(String routeId) {
    consumers.remove(routeId);
  }

}
