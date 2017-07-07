package de.fzi.cep.sepa.axoom.hmi.sources;

import de.fzi.cep.sepa.axoom.hmi.iot.AxoomIotStreamBuilder;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataSourceBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 20.04.2017.
 */
public class AxoomIotHubProducer implements SemanticEventProducerDeclarer {

  @Override
  public SepDescription declareModel() {
    return DataSourceBuilder.create("axoom-hmi-iothub", "Axoom IoT Platform", "Source that " +
            "provides " +
            "data " +
            "from the Axoom IoT Platform")
            .build();
  }

  @Override
  public List<EventStreamDeclarer> getEventStreams() {
    List<EventStreamDeclarer> axoomStreams = new ArrayList<>();

    axoomStreams.addAll(AxoomIotStreamBuilder.buildIotStreams());
    return axoomStreams;
  }
}
