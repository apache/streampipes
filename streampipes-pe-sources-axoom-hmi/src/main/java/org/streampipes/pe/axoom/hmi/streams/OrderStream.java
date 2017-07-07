package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.pe.axoom.hmi.vocabulary.AxoomVocabulary;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;

/**
 * Created by riemer on 16.03.2017.
 */
public class OrderStream extends AbstractAxoomHmiStream {

  public OrderStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    return DataStreamBuilder.create("axoom-orders-" + eventType.getEventType(), "Orders "
                    + eventType.getEventType().toUpperCase(),
            "Provides a stream of " +
                    "current " +
                    "coffee orders")
            .iconUrl(SourceConfig.getIconUrl("coffee-order"))
            .property(EpProperties.stringEp("customer", AxoomVocabulary.Name))
            .property(EpProperties.stringEp("machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.booleanEp("milk", AxoomVocabulary.MilkIncluded))
            .property(EpProperties.booleanEp("sugar", AxoomVocabulary.SugarIncluded))
            .property(EpProperties.stringEp("machineName", AxoomVocabulary.MachineName))
            .property(EpProperties.stringEp("machineDisplayName", AxoomVocabulary
                    .MachineDisplayName))
            .property(EpProperties.longEp("timestamp",
                    "http://schema.org/DateTime"))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic("order")))
            .build();
  }
}
