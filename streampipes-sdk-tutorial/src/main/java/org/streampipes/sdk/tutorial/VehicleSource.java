package org.streampipes.sdk.tutorial;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.sdk.builder.DataSourceBuilder;

import java.util.Arrays;
import java.util.List;


/**
 * Created by riemer on 12.03.2017.
 */
public class VehicleSource implements SemanticEventProducerDeclarer {

  @Override
  public SepDescription declareModel() {
    return DataSourceBuilder.create("source-vehicle", "Vehicle Source", "A data source that " +
            "holds event streams produced by vehicles.")
            .build();
  }

  @Override
  public List<EventStreamDeclarer> getEventStreams() {
    return Arrays.asList(new VehiclePositionStream());
  }
}
