package org.streampipes.wrapper.flink.samples.statistics.window;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.extensions.MapKeySelector;
import org.streampipes.wrapper.flink.extensions.SlidingEventTimeWindow;
import org.streampipes.wrapper.flink.extensions.TimestampMappingFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;
import java.util.Map;

/**
 * Created by riemer on 20.04.2017.
 */
public class StatisticsSummaryProgramWindow extends
        FlinkSepaRuntime<StatisticsSummaryParametersWindow> {

  private StatisticsSummaryParamsSerializable serializableParams;

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params,
                                        StatisticsSummaryParamsSerializable serializableParams) {
    super(params);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    this.serializableParams = serializableParams;
  }

  public StatisticsSummaryProgramWindow(StatisticsSummaryParametersWindow params,
                                        StatisticsSummaryParamsSerializable serializableParams,
                                        FlinkDeploymentConfig config) {
    super(params, config);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
    this.serializableParams = serializableParams;
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {

    StatisticsSummaryParamsSerializable sp = new
            StatisticsSummaryParamsSerializable(serializableParams.getValueToObserve(),
            serializableParams.getTimestampMapping(), serializableParams.getGroupBy(),
            serializableParams.getTimeWindowSize(), serializableParams.getTimeUnit());
    DataStream<Map<String, Object>> output = messageStream[0]
            .keyBy(new MapKeySelector(sp.getGroupBy()).getKeySelector())
            .transform
                    ("sliding-window-event-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingEventTimeWindow<>(sp.getTimeWindowSize(), sp.getTimeUnit(),
                                    (TimestampMappingFunction<Map<String, Object>>) in ->
                                            Long.parseLong(String.valueOf(in.get(sp.getTimestampMapping())))))
            .flatMap(new StatisticsSummaryCalculatorWindow(sp.getGroupBy(), sp.getValueToObserve()));

    return output;
  }




}