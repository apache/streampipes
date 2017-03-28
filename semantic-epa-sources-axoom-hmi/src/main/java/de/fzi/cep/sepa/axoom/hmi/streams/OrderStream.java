package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.axoom.hmi.vocabulary.AxoomVocabulary;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;

/**
 * Created by riemer on 16.03.2017.
 */
public class OrderStream extends AbstractAxoomHmiStream {

  public OrderStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    return DataStreamBuilder.create("axoom-orders-" +eventType.getEventType(), "Orders "
                    +eventType.getEventType().toUpperCase(),
            "Provides a stream of " +
            "current " +
            "coffee orders")
            .property(EpProperties.stringEp("customer", AxoomVocabulary.Name))
            .property(EpProperties.stringEp("machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.integerEp("milk", AxoomVocabulary.MilkIncluded))
            .property(EpProperties.integerEp("sugar", AxoomVocabulary.SugarIncluded))
            .property(EpProperties.stringEp("machineName", AxoomVocabulary.MachineName))
            .property(EpProperties.stringEp("machineDisplayName", AxoomVocabulary
                    .MachineDisplayName))
            .property(EpProperties.stringEp("machineModel", AxoomVocabulary.MachineModel))
            .property(EpProperties.nestedEp("date", EpProperties.longEp("$$date",
                    "http://schema.org/DateTime")))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic("order")))
            .build();
  }
}
