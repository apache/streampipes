package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;

/**
 * Created by riemer on 16.03.2017.
 */
public class TrendStream extends AbstractAxoomHmiStream {


  public TrendStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    return DataStreamBuilder.create("axoom-trend-" +eventType.getEventType(), "Trend "
                    +eventType.getEventType().toUpperCase(),
            "Provides a stream of the " +
                    "current " +
                    "trend")
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic("trend")))
            .build();
  }
}
