package org.streampipes.pe.axoom.hmi.iot;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sources.AbstractAlreadyExistingStream;

public class AxoomIotStream extends AbstractAlreadyExistingStream {
  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return null;
  }

//  private AxoomMachines machine;
//
//  public AxoomIotStream(AxoomMachines axoomMachines) {
//    this.machine = axoomMachines;
//  }
//
//  @Override
//  public EventStream declareModel(SepDescription sep) {
//    DataStreamBuilder builder = DataStreamBuilder.create("axoom-iot-" + machine.getId(),
//            machine.getName(), machine.getManufacturer() + " " + machine.getEquipmentNo())
//            .iconUrl(SourceConfig.getIconUrl(makeIconName()))
//            .format(Formats.jsonFormat())
//            .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
//                    ClientConfiguration.INSTANCE.getKafkaPort(), "axoom.hmi.hmi." + machine.getId()))
//            .property(EpProperties.longEp("timestamp",
//                    "http://schema.org/DateTime"))
//            .property(EpProperties.stringEp("machineId",
//                    "http://schema.org/machineId"));
//
//    for (Sensor sensor : machine.getSensors()) {
//      builder.property(makeProperty(sensor));
//    }
//
//    return builder.build();
//  }
//
//  private String makeIconName() {
//    return machine.getEquipmentNo();
//  }
//
//  private EventProperty makeProperty(Sensor sensor) {
//    String propertyName = sensor.getName();
//    String typeHint = sensor.getTypeHint();
//    if (typeHint.equals("double")) {
//      return EpProperties.doubleEp(propertyName, SO.Number);
//    } else if (typeHint.equals("integer")) {
//      return EpProperties.integerEp(propertyName, SO.Number);
//    } else if (typeHint.equals("boolean")) {
//      return EpProperties.booleanEp(propertyName, SO.Boolean);
//    } else {
//      return EpProperties.stringEp(propertyName, SO.Text);
//    }
//  }
}
