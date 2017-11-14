package org.streampipes.pe.axoom.hmi.sources;

import org.streampipes.pe.axoom.hmi.config.AxoomHmiConfig;
import org.streampipes.pe.axoom.hmi.streams.EnergyStream;
import org.streampipes.pe.axoom.hmi.streams.MaintenanceStream;
import org.streampipes.pe.axoom.hmi.streams.OrderStream;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataSourceBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 16.03.2017.
 */
public class AxoomIoTCafeProducer implements SemanticEventProducerDeclarer {

  @Override
  public DataSourceDescription declareModel() {
    return DataSourceBuilder.create("axoom-hmi-cafe", "Axoom IoT Cafeteria", "Source that " +
            "provides " +
            "data " +
            "from the Axoom IoT Cafe")
            .build();
  }

  @Override
  public List<EventStreamDeclarer> getEventStreams() {
    List<EventStreamDeclarer> axoomStreams = new ArrayList<>(Arrays.asList(//new MachineStream
            // (AxoomHmiConfig
            // .FABTECH),
            //new MaintenanceStream(AxoomHmiConfig.FABTECH),
            //new OrderStream(AxoomHmiConfig.FABTECH),
            //new TrendStream(AxoomHmiConfig.FABTECH),
            //new MachineStream(AxoomHmiConfig.EUROBLECH),
            //new MaintenanceStream(AxoomHmiConfig.EUROBLECH),
            //new OrderStream(AxoomHmiConfig.EUROBLECH),
            new OrderStream(AxoomHmiConfig.HMI),
            new MaintenanceStream(AxoomHmiConfig.HMI),
            new EnergyStream(AxoomHmiConfig.HMI)));
    return axoomStreams;
  }
}
