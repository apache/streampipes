#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.${elementName};

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Date;
import java.util.Map;

public class ${classNamePrefix} implements EventSink<${classNamePrefix}Parameters> {

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
