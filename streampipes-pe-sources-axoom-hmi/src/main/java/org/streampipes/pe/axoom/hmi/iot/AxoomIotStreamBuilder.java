package org.streampipes.pe.axoom.hmi.iot;

import org.streampipes.container.declarer.EventStreamDeclarer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 17.04.2017.
 */
public class AxoomIotStreamBuilder {

  public static List<EventStreamDeclarer> buildIotStreams() {
    List<EventStreamDeclarer> iotStreams = new ArrayList<>();
//    try {
//      Gson gson = new Gson();
//      AxoomMachines[] machineDefinitions = gson.fromJson(new FileReader(getConfigFile()),
//              AxoomMachines[]
//              .class);
//      for(AxoomMachines machineDefinition : machineDefinitions) {
//        iotStreams.add(buildIotStream(machineDefinition));
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

    return iotStreams;
  }

//  private static EventStreamDeclarer buildIotStream(AxoomMachines machineDefinition) {
//    return new AxoomIotStream(machineDefinition);
//  }

}
