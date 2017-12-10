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
import org.apache.commons.lang.WordUtils;

/**
 * Created by riemer on 16.03.2017.
 */
public class MaintenanceStream extends AbstractAxoomHmiStream {

  public MaintenanceStream(AxoomHmiConfig eventType) {
    super(eventType);
  }

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create
            ("axoom-maintenance-" + eventType.getEventType(), "Maintenance "
                            + WordUtils.capitalize(eventType.getEventType()),
                    "Provides a stream of " +
                            "current " +
                            "maintenance events")
            .iconUrl(SourceConfig.getIconUrl("coffee-maintenance"))
            .property(EpProperties.longEp(Labels.empty(), "timestamp", "http://schema.org/DateTime"))
            .property(EpProperties.longEp(Labels.empty(), "maintenanceStartTime", "http://schema.org/DateTime"))
            .property(EpProperties.longEp(Labels.empty(), "maintenanceEndTime", "http://schema.org/DateTime"))
            .property(EpProperties.stringEp(Labels.empty(), "machineId", AxoomVocabulary.MachineId))
            .property(EpProperties.booleanEp(Labels.empty(), "milkRefilled", AxoomVocabulary.MilkRefilled))
            .property(EpProperties.booleanEp(Labels.empty(), "waterRefilled", AxoomVocabulary.SugarRefilled))
            .property(EpProperties.stringEp(Labels.empty(), "state", AxoomVocabulary.State))
            .property(EpProperties.integerEp(Labels.empty(), "totalMaintenanceTime", AxoomVocabulary
                    .MaintenanceTime))
            .property(EpProperties.stringEp(Labels.empty(), "machineName", AxoomVocabulary.MachineName))
            .property(EpProperties.stringEp(Labels.empty(), "machineDisplayName", AxoomVocabulary
                    .MachineDisplayName))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourceConfig.INSTANCE.getKafkaHost(),
                    SourceConfig.INSTANCE.getKafkaPort(), eventType.getTopic
                            ("maintenance")))
            .build();
  }
}
