#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import org.slf4j.Logger;

public class ${classNamePrefix} implements
        EventProcessor<${classNamePrefix}Parameters> {

  private static Logger LOG;

  @Override
  public void onInvocation(${classNamePrefix}Parameters parameters,
        SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {

  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {

  }

  @Override
  public void onDetach() {

  }
}
