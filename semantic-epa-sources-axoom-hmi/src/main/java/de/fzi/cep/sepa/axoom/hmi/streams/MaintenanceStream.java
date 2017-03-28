package de.fzi.cep.sepa.axoom.hmi.streams;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
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
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), eventType.getTopic
                            ("maintenance")))
            .build();
  }
}
