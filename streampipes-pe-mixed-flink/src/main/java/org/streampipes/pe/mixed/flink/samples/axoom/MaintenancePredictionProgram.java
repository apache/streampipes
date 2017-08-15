package org.streampipes.pe.mixed.flink.samples.axoom;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.extensions.SlidingEventTimeWindow;
import org.streampipes.pe.mixed.flink.extensions.TimestampMappingFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionProgram extends FlinkSepaRuntime<MaintenancePredictionParameters> {

  public MaintenancePredictionProgram(MaintenancePredictionParameters params) {
    super(params);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
  }

  public MaintenancePredictionProgram(MaintenancePredictionParameters params, FlinkDeploymentConfig config) {
    super(params, config);
    this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {
    DataStream<List<Map<String, Object>>> lastOrderStream = messageStream[0]
            .keyBy(getKeySelector())
            .transform
                    ("sliding-window-event-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingEventTimeWindow<>(60l, TimeUnit.MINUTES,
                                    (TimestampMappingFunction<Map<String, Object>>) in ->
                                            Long.parseLong(String.valueOf(in.get("timestamp")))));


    DataStream<Map<String, Object>> maintenanceStream = messageStream[1]
            .keyBy(getKeySelector());

    ConnectedStreams<List<Map<String, Object>>, Map<String, Object>>
            connectedStream = lastOrderStream.connect(maintenanceStream).keyBy(getKeySelectorList
            (), getKeySelector());

    return connectedStream.flatMap(new MaintenancePrediction());


  }

  private KeySelector<List<Map<String, Object>>, String> getKeySelectorList() {

    return new KeySelector<List<Map<String, Object>>, String>() {
      @Override
      public String getKey(List<Map<String, Object>> maps) throws Exception {
        return String.valueOf(maps.get(maps.size()-1).get("machineId"));
      }
    };
  }

  private KeySelector<Map<String, Object>, String> getKeySelector() {
    return new KeySelector<Map<String, Object>, String>() {
      @Override
      public String getKey(Map<String, Object> in) throws Exception {
        return String.valueOf(in.get("machineId"));
      }
    };
  }
}
