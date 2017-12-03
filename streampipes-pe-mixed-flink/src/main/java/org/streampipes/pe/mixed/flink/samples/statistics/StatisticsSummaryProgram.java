package org.streampipes.pe.mixed.flink.samples.statistics;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryProgram extends FlinkDataProcessorRuntime<StatisticsSummaryParameters> {

  public StatisticsSummaryProgram(StatisticsSummaryParameters params) {
    super(params);
  }

  public StatisticsSummaryProgram(StatisticsSummaryParameters params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
    return messageStream[0].flatMap(new StatisticsSummaryCalculator(params.getListPropertyName()));
  }
}
