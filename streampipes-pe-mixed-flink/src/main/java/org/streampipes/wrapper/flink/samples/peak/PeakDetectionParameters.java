package org.streampipes.wrapper.flink.samples.peak;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

import java.io.Serializable;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionParameters extends BindingParameters implements Serializable {

  private String valueToObserve;
  private String timestampMapping;
  private String groupBy;
  private Integer lag;
  private Double threshold;
  private Double influence;
  private Integer countWindowSize;

  public PeakDetectionParameters(SepaInvocation graph) {
    super(graph);
  }

  public PeakDetectionParameters(SepaInvocation graph, String valueToObserve, String
          timestampMapping, String groupBy, Integer countWindowSize, Integer lag, Double
          threshold, Double
          influence) {
    super(graph);
    this.valueToObserve = valueToObserve;
    this.timestampMapping = timestampMapping;
    this.groupBy = groupBy;
    this.lag = lag;
    this.threshold = threshold;
    this.influence = influence;
    this.countWindowSize = countWindowSize;
  }

  public String getValueToObserve() {
    return valueToObserve;
  }

  public String getTimestampMapping() {
    return timestampMapping;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public Integer getLag() {
    return lag;
  }

  public Double getThreshold() {
    return threshold;
  }

  public Double getInfluence() {
    return influence;
  }

  public Integer getCountWindowSize() {
    return countWindowSize;
  }
}
