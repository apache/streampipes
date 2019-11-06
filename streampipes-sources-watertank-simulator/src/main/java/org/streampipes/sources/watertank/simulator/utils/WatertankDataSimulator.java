/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sources.watertank.simulator.utils;

import net.acesinc.data.json.generator.config.JSONConfigReader;
import net.acesinc.data.json.generator.config.SimulationConfig;
import net.acesinc.data.json.generator.config.WorkflowConfig;
import org.streampipes.pe.simulator.StreamPipesSimulationRunner;
import org.streampipes.pe.simulator.TopicAwareWorkflow;
import org.streampipes.sources.watertank.simulator.config.WatertankSimulatorConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WatertankDataSimulator implements Runnable {

  private static final String EXAMPLES_CONFIG_FILE = "streampipesDemoConfig.json";

  private void initSimulation() {
    try {
      SimulationConfig config = buildSimulationConfig();
      Map<String, TopicAwareWorkflow> workflows = buildSimWorkflows(config);
      String kafkaHost = WatertankSimulatorConfig.INSTANCE.getKafkaHost();
      Integer kafkaPort = WatertankSimulatorConfig.INSTANCE.getKafkaPort();
      new StreamPipesSimulationRunner(config, workflows, kafkaHost, kafkaPort).startSimulation();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private SimulationConfig buildSimulationConfig() throws IOException {
    return JSONConfigReader.readConfig(WatertankDataSimulator
                    .class
                    .getClassLoader()
                    .getResourceAsStream(EXAMPLES_CONFIG_FILE),
            SimulationConfig.class);
  }

  private Map<String, TopicAwareWorkflow> buildSimWorkflows(SimulationConfig config) throws IOException {
    Map<String, TopicAwareWorkflow> workflows = new HashMap<>();
    for(WorkflowConfig workflowConfig : config.getWorkflows()) {
      workflows.put(workflowConfig.getWorkflowFilename(), JSONConfigReader.readConfig(WatertankDataSimulator.class
                      .getClassLoader().getResourceAsStream(workflowConfig.getWorkflowFilename()),
              TopicAwareWorkflow.class));
    }

    return workflows;
  }

  @Override
  public void run() {
    initSimulation();
  }

  public static void main(String[] args) {
    new WatertankDataSimulator().run();
  }
}
