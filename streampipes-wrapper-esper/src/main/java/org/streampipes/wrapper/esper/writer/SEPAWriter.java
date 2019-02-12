package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.client.EventBean;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.EventFactory;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;

import java.util.Map;

public class SEPAWriter implements Writer {

  private SpOutputCollector collector;
  private EventProcessorRuntimeContext runtimeContext;

  public SEPAWriter(SpOutputCollector collector, EventProcessorRuntimeContext runtimeContext) {
    this.collector = collector;
    this.runtimeContext = runtimeContext;
  }

  @Override
  public void onEvent(EventBean bean) {
    //System.out.println(new Gson().toJson(bean.getUnderlying()));
    Event event = EventFactory.fromMap((Map<String, Object>) bean.getUnderlying(), runtimeContext
                    .getOutputSourceInfo(),
            runtimeContext.getOutputSchemaInfo());
    collector.collect(event);
  }

}
