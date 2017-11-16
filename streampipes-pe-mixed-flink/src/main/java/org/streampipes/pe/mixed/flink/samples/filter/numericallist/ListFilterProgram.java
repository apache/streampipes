package org.streampipes.pe.mixed.flink.samples.filter.numericallist;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

import java.io.Serializable;
import java.util.Map;

public class ListFilterProgram extends FlinkSepaRuntime<ListFilterParameters>
        implements Serializable {

  public ListFilterProgram(ListFilterParameters params,
                            FlinkDeploymentConfig config) {
    super(params, config);
  }

  public ListFilterProgram(ListFilterParameters params) {
    super(params);
  }


  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
    return messageStream[0].flatMap(new ListFilter(params.getPropertyName(),
            params.getFilterKeywords(), params.getFilterOperation(), params.getFilterSettings()));
  }
}
