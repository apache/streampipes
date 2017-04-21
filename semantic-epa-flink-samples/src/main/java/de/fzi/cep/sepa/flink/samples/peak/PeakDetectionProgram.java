package de.fzi.cep.sepa.flink.samples.peak;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.extensions.SlidingBatchWindow;
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
    return messageStream[0]
            .keyBy(getKeySelector())
            .transform
                    ("sliding-batch-window-shift",
                            TypeInformation.of(new TypeHint<List<Map<String, Object>>>() {
                            }), new SlidingBatchWindow<>(params.getLag() +2))
            .flatMap(new PeakDetectionCalculator(params.getGroupBy(),
                    params.getValueToObserve(),
                    params.getLag(),
                    params.getThreshold(),
                    params.getInfluence()));
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
