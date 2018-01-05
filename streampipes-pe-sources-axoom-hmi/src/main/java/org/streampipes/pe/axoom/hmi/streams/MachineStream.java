package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;

public class MachineStream extends AbstractAxoomHmiStream {

  public MachineStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("axoom-machine-" +eventType.getEventType(), "Machine "
                    +eventType.getEventType().toUpperCase(),
            "Provides a stream of " +
                    "machine " +
                    "events")
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic("machine")))
            .build();
  }
}
