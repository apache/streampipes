package org.streampipes.pe.axoom.hmi.streams;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.pe.axoom.hmi.config.SourceConfig;
import org.streampipes.pe.axoom.hmi.vocabulary.AxoomVocabulary;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.apache.commons.lang.WordUtils;

/**
 * Created by riemer on 16.03.2017.
 */
public class MaintenanceStream extends AbstractAxoomHmiStream {

  public MaintenanceStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    return DataStreamBuilder.create
            ("axoom-maintenance-" + eventType.getEventType(), "Maintenance "
                            + WordUtils.capitalize(eventType.getEventType()),
                    "Provides a stream of " +
                            "current " +
                            "maintenance events")
            .iconUrl(SourceConfig.getIconUrl("coffee-maintenance"))
            .property(EpProperties.longEp("timestamp", "http://schema.org/DateTime"))
            .property(EpProperties.longEp("maintenanceStartTime", "http://schema.org/DateTime"))
            .property(EpProperties.longEp("maintenanceEndTime", "http://schema.org/DateTime"))
            .property(EpProperties.stringEp("machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.booleanEp("milkRefilled", AxoomVocabulary.MilkRefilled))
            .property(EpProperties.booleanEp("waterRefilled", AxoomVocabulary.SugarRefilled))
            .property(EpProperties.stringEp("state", AxoomVocabulary.State))
            .property(EpProperties.integerEp("totalMaintenanceTime", AxoomVocabulary
                    .MaintenanceTime))
            .property(EpProperties.stringEp("machineName", AxoomVocabulary.MachineName))
            .property(EpProperties.stringEp("machineDisplayName", AxoomVocabulary
                    .MachineDisplayName))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic
                            ("maintenance")))
            .build();
  }
}
