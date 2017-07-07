package de.fzi.cep.sepa.flink.samples.spatial.gridenricher;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.Map;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridEnrichmentProgram extends FlinkSepaRuntime<SpatialGridEnrichmentParameters> {

  public SpatialGridEnrichmentProgram(SpatialGridEnrichmentParameters params) {
    super(params);
  }

  public SpatialGridEnrichmentProgram(SpatialGridEnrichmentParameters params, FlinkDeploymentConfig config) {
    super(params, config);
  }

  @Override
  protected DataStream<Map<String, Object>> getApplicationLogic(DataStream<Map<String, Object>>[] messageStream) {
    return messageStream[0].flatMap(new SpatialGridEnricher(params.getEnrichmentSettings()));
  }
}
