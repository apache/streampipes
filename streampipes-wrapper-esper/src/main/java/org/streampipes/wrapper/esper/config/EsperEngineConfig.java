package org.streampipes.wrapper.esper.config;

import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.esper.writer.SEPAWriter;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.routing.SpOutputCollector;

public class EsperEngineConfig {


  public static <T> Writer getDefaultWriter(SpOutputCollector collector, EventProcessorRuntimeContext runtimeContext) {
    return new SEPAWriter(collector, runtimeContext);
  }
}
