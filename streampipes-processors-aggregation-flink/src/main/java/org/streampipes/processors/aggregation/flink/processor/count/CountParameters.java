package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CountParameters extends EventProcessorBindingParams {

  private Time time;
  private String fieldToCount;

  public CountParameters(DataProcessorInvocation graph, Time time, String fieldToCount) {
    super(graph);
    this.time = time;
    this.fieldToCount = fieldToCount;
  }

  public Time getTime() {
    return time;
  }

  public String getFieldToCount() {
    return fieldToCount;
  }
}