package de.fzi.cep.sepa.flink.samples.axoom;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by riemer on 13.04.2017.
 */
public class MaintenancePrediction extends RichCoFlatMapFunction<List<Map<String,
        Object>>, Map<String, Object>, Map<String,Object>> implements Serializable {

  private transient ValueState<Integer> maxOrdersBetweenMaintenance;
  private transient ValueState<List<Map<String, Object>>> ordersSinceLastMaintenance;
  private transient ValueState<String> partitionUuid;

  // Orders
  @Override
  public void flatMap1(List<Map<String, Object>> in, Collector<Map<String, Object>> out) throws
          Exception {
    Map<String, Object> recentOrder = in.get(in.size()-1);
    List<Map<String, Object>> orderList = ordersSinceLastMaintenance.value();
    orderList.add(recentOrder);
    ordersSinceLastMaintenance.update(orderList);
    Integer remainingOrdersUntilFailure =  maxOrdersBetweenMaintenance.value() -
            ordersSinceLastMaintenance.value().size();

    Long predictedBreakdownFromNow = computePredictedMaintenanceTime(in,
            remainingOrdersUntilFailure);
    Long predictedMaintenanceTime = Long.parseLong(extractFromMap("timestamp", recentOrder))
            +predictedBreakdownFromNow;

    String maintenanceDate = new Date(predictedMaintenanceTime).toGMTString();

    System.out.println("Predicted Maintenance (" +recentOrder.get("machineId") +") : "
            +maintenanceDate);

    out.collect(makeMaintenancePredictionEvent(extractFromMap("machineId", recentOrder), predictedMaintenanceTime));
  }

  private Map<String,Object> makeMaintenancePredictionEvent(String machineId, Long predictedMaintenanceTime) {
    Map<String, Object> result = new HashMap<>();
    result.put("timestamp", System.currentTimeMillis());
    result.put("machineId", machineId);
    result.put("predictedMaintenanceTime", predictedMaintenanceTime);
    return result;
  }

  private Long computePredictedMaintenanceTime(List<Map<String, Object>> in, Integer remainingOrdersUntilFailure) {
    Double ordersPerMinute = (((double) in.size()) / Math.max(1, calculateTimeDiff(in)));

    Double minutesUntilFailure = remainingOrdersUntilFailure / ordersPerMinute;
    return TimeUnit.MINUTES.toMillis(Math.round(minutesUntilFailure));
  }

  private Long calculateTimeDiff(List<Map<String, Object>> in) {
    Long timestampFirst = Long.parseLong(String.valueOf(in.get(0).get("timestamp")));
    Long timestampLast = Long.parseLong(String.valueOf(in.get(in.size() -1).get("timestamp")));

    Long timeDiffInSeconds = TimeUnit.MILLISECONDS.toMinutes(timestampLast - timestampFirst);
    return timeDiffInSeconds;
  }

  private String extractFromMap(String key, Map<String, Object> map) {
    return String.valueOf(map.get(key));
  }

  // Maintenances
  @Override
  public void flatMap2(Map<String, Object> in, Collector<Map<String, Object>> out) throws
          Exception {
    List<Map<String, Object>> orders = ordersSinceLastMaintenance.value();
    System.out.println("Maintenance occured (" +in.get("machineId") +"): " +new Date(Long
            .parseLong(String.valueOf(in.get("maintenanceStartTime")))).toGMTString());
    if (orders.size() > maxOrdersBetweenMaintenance.value()) {
      maxOrdersBetweenMaintenance.update(ordersSinceLastMaintenance.value().size());
      System.out.println("Setting new max order value for machine " +in.get("machineId") +": "
              +maxOrdersBetweenMaintenance.value());
    }
    ordersSinceLastMaintenance.update(new ArrayList<>());
  }

  @Override
  public void open(Configuration config) {
    maxOrdersBetweenMaintenance = this.getRuntimeContext().getState(getValueStateDescriptor
            ("maxOrdersBetweenMaintenance", 0, new TypeHint<Integer>(){}));
    partitionUuid = this.getRuntimeContext().getState(getValueStateDescriptor
            ("partitionUuid", UUID.randomUUID().toString(), new TypeHint<String>(){}));
    ordersSinceLastMaintenance = this.getRuntimeContext().getState(getValueStateDescriptor
            ("ordersSinceLastMaintenance", new ArrayList<>(), new
                    TypeHint<List<Map<String, Object>>>(){}));

  }

  private <T> ValueStateDescriptor<T> getValueStateDescriptor(String name, T defaultValue,
                                                              TypeHint<T> typeHint) {
    return new ValueStateDescriptor<>(name, TypeInformation.of(typeHint), defaultValue);
  }
}
