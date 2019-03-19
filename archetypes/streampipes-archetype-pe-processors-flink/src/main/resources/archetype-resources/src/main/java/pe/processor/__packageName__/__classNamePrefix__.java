#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import org.streampipes.model.runtime.Event;

public class ${classNamePrefix} implements FlatMapFunction<Event, Event> {

  @Override
  public void flatMap(Event in, Collector<Event> out) throws Exception {

    out.collect(in);
  }
}
