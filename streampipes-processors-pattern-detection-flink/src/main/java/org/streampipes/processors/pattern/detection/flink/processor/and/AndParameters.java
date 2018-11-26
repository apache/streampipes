package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class AndParameters extends EventProcessorBindingParams {

  private TimeUnit timeUnit;
  private Integer timeWindow;

  private List<String> leftMappings;
  private List<String> rightMappings;


  public AndParameters(DataProcessorInvocation invocationGraph, TimeUnit timeUnit, Integer timeWindow, List<String> leftMappings, List<String> rightMappings) {
    super(invocationGraph);
    this.timeUnit = timeUnit;
    this.timeWindow = timeWindow;
    this.leftMappings = leftMappings;
    this.rightMappings = rightMappings;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public Integer getTimeWindow() {
    return timeWindow;
  }

  public List<String> getLeftMappings() {
    return leftMappings;
  }

  public List<String> getRightMappings() {
    return rightMappings;
  }
}
