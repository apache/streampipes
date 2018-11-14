#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.processor.${packageName};

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class ${classNamePrefix} implements FlatMapFunction<Map<String, Object>, Map<String,
        Object>> {

  @Override
  public void flatMap(Map<String, Object> in,
                      Collector<Map<String, Object>> out) throws Exception {

    out.collect(in);
  }
}
