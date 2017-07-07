package de.fzi.cep.sepa.axoom.hmi.sources;

import de.fzi.cep.sepa.axoom.hmi.config.AxoomHmiConfig;
import de.fzi.cep.sepa.axoom.hmi.streams.EnergyStream;
import de.fzi.cep.sepa.axoom.hmi.streams.MaintenanceStream;
import de.fzi.cep.sepa.axoom.hmi.streams.OrderStream;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataSourceBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 16.03.2017.
 */
public class AxoomIoTCafeProducer implements SemanticEventProducerDeclarer {

  @Override
  public SepDescription declareModel() {
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
