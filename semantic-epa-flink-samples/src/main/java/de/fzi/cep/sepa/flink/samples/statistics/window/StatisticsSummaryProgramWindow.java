package de.fzi.cep.sepa.flink.samples.statistics.window;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.extensions.SlidingEventTimeWindow;
import de.fzi.cep.sepa.flink.extensions.TimestampMappingFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;
import java.util.Map;

/**
 * Created by riemer on 20.04.2017.
 */
public class StatisticsSummaryProgramWindow extends
        FlinkSepaRuntime<StatisticsSummaryParametersWindow> {

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params) {
    super(params);
  }

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params,
                                        FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
    DataStream<Map<String, Object>> statisticsStream = messageStream[0]
            .keyBy(getKeySelector())
            .transform
                    ("sliding-window-event-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingEventTimeWindow<>(params.getTimeWindowSize(), params.getTimeUnit(),
                                    (TimestampMappingFunction<Map<String, Object>>) in ->
                                            Long.parseLong(String.valueOf(in.get(params.getTimestampMapping())))))
            .flatMap(new StatisticsSummaryCalculatorWindow(params.getGroupBy(), params.getValueToObserve()));

    return statisticsStream;
  }

  private KeySelector<Map<String, Object>, String> getKeySelector() {
    return new KeySelector<Map<String, Object>, String>() {
      @Override
      public String getKey(Map<String, Object> in) throws Exception {
        return String.valueOf(in.get(getGroupBy()));
      }
    };
  }

  private String getGroupBy() {
    return params.getGroupBy();
  }
}