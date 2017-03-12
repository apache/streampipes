package de.fzi.cep.sepa.flink.samples.statistics;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryProgram extends FlinkSepaRuntime<StatisticsSummaryParameters> {

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
