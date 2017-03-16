package de.fzi.cep.sepa.axoom.hmi.sources;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.axoom.hmi.streams.MachineStream;
import de.fzi.cep.sepa.axoom.hmi.streams.MaintenanceStream;
import de.fzi.cep.sepa.axoom.hmi.streams.OrderStream;
import de.fzi.cep.sepa.axoom.hmi.streams.TrendStream;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataSourceBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 16.03.2017.
 */
public class AxoomHmiProducer implements SemanticEventProducerDeclarer {

  @Override
  public SepDescription declareModel() {
    return DataSourceBuilder.create("axoom-hmi", "Axoom Data Source", "Source that produces data " +
            "for the Axoom HMI showcase")
            .build();
  }

  @Override
  public List<EventStreamDeclarer> getEventStreams() {
    return Arrays.asList(new MachineStream(),
            new MaintenanceStream(),
            new OrderStream(AxoomHmiConfig.EUROBLECH),
            new OrderStream(AxoomHmiConfig.FABTECH),
            new TrendStream());
  }
}
