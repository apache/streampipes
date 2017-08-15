package org.streampipes.pe.mixed.flink.samples.peak;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.extensions.SlidingBatchWindow;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;
import java.util.Map;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionProgram extends FlinkSepaRuntime<PeakDetectionParameters> {

  public PeakDetectionProgram(PeakDetectionParameters params) {
    super(params);
  }

  public PeakDetectionProgram(PeakDetectionParameters params,
                              FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {

    Integer lag = params.getLag();
    String groupBy = params.getGroupBy();
    String valueToObserve = params.getValueToObserve();
    Double threshold = params.getThreshold();
    Double influence = params.getInfluence();
    Integer countWindowSize = params.getCountWindowSize();

    return messageStream[0]
            .keyBy(getKeySelector())
            .transform
                    ("sliding-batch-window-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingBatchWindow<>(countWindowSize))
            .flatMap(new PeakDetectionCalculator(groupBy,
                    valueToObserve,
                    lag,
                    threshold,
                    influence));
  }

  private KeySelector<Map<String, Object>, String> getKeySelector() {
    String groupBy = params.getGroupBy();
    return new KeySelector<Map<String, Object>, String>() {
      @Override
      public String getKey(Map<String, Object> in) throws Exception {
        return String.valueOf(in.get(groupBy));
      }
    };
  }
}
