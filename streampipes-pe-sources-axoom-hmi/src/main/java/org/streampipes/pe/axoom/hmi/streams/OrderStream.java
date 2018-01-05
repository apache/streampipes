package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.pe.axoom.hmi.vocabulary.AxoomVocabulary;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;

public class OrderStream extends AbstractAxoomHmiStream {

  public OrderStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("axoom-orders-" + eventType.getEventType(), "Orders "
                    + eventType.getEventType().toUpperCase(),
            "Provides a stream of " +
                    "current " +
                    "coffee orders")
            .iconUrl(SourceConfig.getIconUrl("coffee-order"))
            .property(EpProperties.stringEp(Labels.empty(), "customer", AxoomVocabulary.Name))
            .property(EpProperties.stringEp(Labels.empty(), "machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.booleanEp(Labels.empty(), "milk", AxoomVocabulary.MilkIncluded))
            .property(EpProperties.booleanEp(Labels.empty(), "sugar", AxoomVocabulary.SugarIncluded))
            .property(EpProperties.stringEp(Labels.empty(), "machineName", AxoomVocabulary.MachineName))
            .property(EpProperties.stringEp(Labels.empty(), "machineDisplayName", AxoomVocabulary
                    .MachineDisplayName))
            .property(EpProperties.longEp(Labels.empty(), "timestamp",
                    "http://schema.org/DateTime"))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic("order")))
            .build();
  }
}
