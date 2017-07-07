package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.axoom.hmi.config.SourceConfig;
import de.fzi.cep.sepa.axoom.hmi.vocabulary.AxoomVocabulary;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;
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
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic
                            ("maintenance")))
            .build();
  }
}
