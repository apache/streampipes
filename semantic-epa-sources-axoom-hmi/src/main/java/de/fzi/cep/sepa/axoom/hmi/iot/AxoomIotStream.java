package de.fzi.cep.sepa.axoom.hmi.iot;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.streampipes.adapter.axoom.model.AxoomMachines;
import de.fzi.streampipes.adapter.axoom.model.Sensor;

/**
 * Created by riemer on 17.04.2017.
 */
public class AxoomIotStream extends AbstractAlreadyExistingStream {

  private AxoomMachines machine;

  public AxoomIotStream(AxoomMachines axoomMachines) {
    this.machine = axoomMachines;
  }

  @Override
  public EventStream declareModel(SepDescription sep) {
    DataStreamBuilder builder = DataStreamBuilder.create("axoom-iot-" + machine.getId(),
            machine.getName(), machine.getManufacturer() + " " + machine.getEquipmentNo())
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                    ClientConfiguration.INSTANCE.getKafkaPort(), "axoom.hmi.hmi." + machine.getId()))
            .property(EpProperties.longEp("timestamp",
                    "http://schema.org/DateTime"));

    for (Sensor sensor : machine.getSensors()) {
      builder.property(makeProperty(sensor));
    }

    return builder.build();
  }

  private EventProperty makeProperty(Sensor sensor) {
    String propertyName = sensor.getName();
    String typeHint = sensor.getTypeHint();
    if (typeHint.equals("double")) {
      return EpProperties.doubleEp(propertyName, SO.Number);
    } else if (typeHint.equals("integer")) {
      return EpProperties.integerEp(propertyName, SO.Number);
    } else if (typeHint.equals("boolean")) {
      return EpProperties.booleanEp(propertyName, SO.Boolean);
    } else {
      return EpProperties.stringEp(propertyName, SO.Text);
    }
  }
}
