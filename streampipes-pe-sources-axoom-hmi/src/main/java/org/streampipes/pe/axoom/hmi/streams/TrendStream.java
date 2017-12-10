package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
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
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("axoom-trend-" +eventType.getEventType(), "Trend "
                    +eventType.getEventType().toUpperCase(),
            "Provides a stream of the " +
                    "current " +
                    "trend")
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic("trend")))
            .build();
  }
}
