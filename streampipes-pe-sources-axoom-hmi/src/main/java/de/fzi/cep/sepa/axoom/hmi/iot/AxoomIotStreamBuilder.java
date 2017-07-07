package de.fzi.cep.sepa.axoom.hmi.iot;

import com.google.gson.Gson;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.streampipes.adapter.axoom.model.AxoomMachines;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 17.04.2017.
 */
public class AxoomIotStreamBuilder {

  public static List<EventStreamDeclarer> buildIotStreams() {
    List<EventStreamDeclarer> iotStreams = new ArrayList<>();
    try {
      Gson gson = new Gson();
      AxoomMachines[] machineDefinitions = gson.fromJson(new FileReader(getConfigFile()),
              AxoomMachines[]
              .class);
      for(AxoomMachines machineDefinition : machineDefinitions) {
        iotStreams.add(buildIotStream(machineDefinition));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return iotStreams;
  }

  private static EventStreamDeclarer buildIotStream(AxoomMachines machineDefinition) {
    return new AxoomIotStream(machineDefinition);
  }

  private static File getConfigFile() throws IOException {
    return ConfigurationManager.getConfigFile("machines-and-sensors-cloud.json");
  }
}
