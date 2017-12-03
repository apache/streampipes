package org.streampipes.pe.mixed.flink.samples.breakdown;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownProgram extends FlinkDataProcessorRuntime<Prediction2BreakdownParameters> {

  public Prediction2BreakdownProgram(Prediction2BreakdownParameters params) {
    super(params);
  }

  public Prediction2BreakdownProgram(Prediction2BreakdownParameters params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>... messageStream) {
    return messageStream[0].flatMap(new Prediction2BreakDownMapper());
  }
}
