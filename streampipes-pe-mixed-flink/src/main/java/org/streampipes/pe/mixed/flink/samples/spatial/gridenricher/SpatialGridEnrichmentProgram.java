package org.streampipes.pe.mixed.flink.samples.spatial.gridenricher;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
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
