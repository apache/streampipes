#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.sink.${packageName};

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import org.slf4j.Logger;

import java.util.Map;

public class ${classNamePrefix} extends EventSink<${classNamePrefix}Parameters> {

private static Logger LOG;

  public ${classNamePrefix}(${classNamePrefix}Parameters params) {
        super(params);
  }

  @Override
  public void bind(${classNamePrefix}Parameters parameters) throws SpRuntimeException {

  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {

  }

  @Override
  public void discard() throws SpRuntimeException {

  }
}
