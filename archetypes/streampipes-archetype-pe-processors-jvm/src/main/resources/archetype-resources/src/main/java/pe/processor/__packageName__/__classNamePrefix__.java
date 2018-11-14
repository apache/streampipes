#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import org.slf4j.Logger;

import java.util.Map;

public class ${classNamePrefix} extends StandaloneEventProcessorEngine<${classNamePrefix}Parameters> {

  private static Logger LOG;

  public ${classNamePrefix}(${classNamePrefix}Parameters params) {
        super(params);
  }

  @Override
  public void onInvocation(${classNamePrefix}Parameters parameters,
        DataProcessorInvocation graph) {

  }

  @Override
  public void onEvent(Map<String, Object> in, String sourceInfo, SpOutputCollector out) {

  }

  @Override
  public void onDetach() {

  }
}
