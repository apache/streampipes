package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.axoom.hmi.config.SourceConfig;
import de.fzi.cep.sepa.axoom.hmi.vocabulary.AxoomVocabulary;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;

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
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic("energy")))
            .property(EpProperties.stringEp("machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.longEp("timestamp",
                    "http://schema.org/DateTime"))
            .property(EpProperties.doubleEp("power", SO.Number))
            .property(EpProperties.doubleEp("voltage", SO.Number))
            .property(EpProperties.doubleEp("current", SO.Number))
            .build();
  }
}
