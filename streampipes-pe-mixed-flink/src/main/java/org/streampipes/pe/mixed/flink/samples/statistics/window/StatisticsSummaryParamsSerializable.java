package org.streampipes.pe.mixed.flink.samples.statistics.window;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class StatisticsSummaryParamsSerializable implements Serializable {

  private String valueToObserve;
  private String timestampMapping;
  private String groupBy;
  private long timeWindowSize;
  private TimeUnit timeUnit;

  public StatisticsSummaryParamsSerializable(String valueToObserve, String timestampMapping,
                                             String groupBy, Long timeWindowSize, TimeUnit
                                                     timeUnit) {
    this.valueToObserve = valueToObserve;
    this.timestampMapping = timestampMapping;
    this.groupBy = groupBy;
    this.timeWindowSize = timeWindowSize;
    this.timeUnit = timeUnit;
  }


  public String getValueToObserve() {
    return valueToObserve;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public Long getTimeWindowSize() {
    return timeWindowSize;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public String getTimestampMapping() {
    return timestampMapping;
  }
}
