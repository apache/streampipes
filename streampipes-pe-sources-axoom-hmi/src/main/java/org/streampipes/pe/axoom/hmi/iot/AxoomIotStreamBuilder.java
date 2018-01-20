package org.streampipes.pe.axoom.hmi.iot;

import org.streampipes.container.declarer.DataStreamDeclarer;

import java.util.ArrayList;
import java.util.List;

public class AxoomIotStreamBuilder {

  public static List<DataStreamDeclarer> buildIotStreams() {
    List<DataStreamDeclarer> iotStreams = new ArrayList<>();
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
