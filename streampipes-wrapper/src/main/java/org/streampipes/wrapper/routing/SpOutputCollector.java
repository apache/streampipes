package org.streampipes.wrapper.routing;

import org.streampipes.messaging.InternalEventProcessor;

import java.util.Map;

public interface SpOutputCollector extends PipelineElementCollector<InternalEventProcessor<Map<String,
        Object>>> {

  void onEvent(Map<String, Object> event);
}
