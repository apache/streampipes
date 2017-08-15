package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.pe.axoom.hmi.vocabulary.AxoomVocabulary;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;

/**
 * Created by riemer on 17.04.2017.
 */
public class EnergyStream extends AbstractAxoomHmiStream {

  public EnergyStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    return DataStreamBuilder.create("axoom-energy-" +eventType.getEventType(), "Energy "
                    +eventType.getEventType().toUpperCase(),
            "Provides a stream of " +
                    "energy " +
                    "events")
            .iconUrl(SourceConfig.getIconUrl("coffee-energy"))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic("energy")))
            .property(EpProperties.stringEp("machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.longEp("timestamp",
                    "http://schema.org/DateTime"))
            .property(EpProperties.doubleEp("power", SO.Number))
            .property(EpProperties.doubleEp("voltage", SO.Number))
            .property(EpProperties.doubleEp("current", SO.Number))
            .build();
  }
}
