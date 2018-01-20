package org.streampipes.pe.axoom.hmi.sources;

import org.streampipes.pe.axoom.hmi.iot.AxoomIotStreamBuilder;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataSourceBuilder;

import java.util.ArrayList;
import java.util.List;

public class AxoomIotHubProducer implements SemanticEventProducerDeclarer {

  @Override
  public DataSourceDescription declareModel() {
    return DataSourceBuilder.create("axoom-hmi-iothub", "Axoom IoT Platform", "Source that " +
            "provides " +
            "data " +
            "from the Axoom IoT Platform")
            .build();
  }

  @Override
  public List<DataStreamDeclarer> getEventStreams() {
    List<DataStreamDeclarer> axoomStreams = new ArrayList<>();

    axoomStreams.addAll(AxoomIotStreamBuilder.buildIotStreams());
    return axoomStreams;
  }
}
