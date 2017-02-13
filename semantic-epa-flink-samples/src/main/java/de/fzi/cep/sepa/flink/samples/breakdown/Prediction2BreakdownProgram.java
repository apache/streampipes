package de.fzi.cep.sepa.flink.samples.breakdown;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownProgram extends FlinkSepaRuntime<Prediction2BreakdownParameters> {

  public Prediction2BreakdownProgram(Prediction2BreakdownParameters params) {
    super(params);
  }

  public Prediction2BreakdownProgram(Prediction2BreakdownParameters params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>> messageStream) {
    return messageStream.flatMap(new Prediction2BreakDownMapper());
  }
}
