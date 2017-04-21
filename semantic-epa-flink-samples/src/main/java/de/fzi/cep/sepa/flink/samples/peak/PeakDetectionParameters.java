package de.fzi.cep.sepa.flink.samples.peak;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionParameters extends BindingParameters {

  private String valueToObserve;
  private String timestampMapping;
  private String groupBy;
  private Integer lag;
  private Double threshold;
  private Double influence;

  public PeakDetectionParameters(SepaInvocation graph) {
    super(graph);
  }

  public PeakDetectionParameters(SepaInvocation graph, String valueToObserve, String timestampMapping, String groupBy, Integer lag, Double threshold, Double influence) {
    super(graph);
    this.valueToObserve = valueToObserve;
    this.timestampMapping = timestampMapping;
    this.groupBy = groupBy;
    this.lag = lag;
    this.threshold = threshold;
    this.influence = influence;
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
}
