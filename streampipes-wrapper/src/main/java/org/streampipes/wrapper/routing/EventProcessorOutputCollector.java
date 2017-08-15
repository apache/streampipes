package org.streampipes.wrapper.routing;

import org.streampipes.messaging.InternalEventProcessor;

import java.util.Map;

public interface EventProcessorOutputCollector extends PipelineElementCollector<InternalEventProcessor<Map<String,
        Object>>> {

  void onEvent(Map<String, Object> event);
}
