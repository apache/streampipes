package de.fzi.cep.sepa.tutorial;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataSourceBuilder;

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
