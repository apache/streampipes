/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.pe.simulator;

import net.acesinc.data.json.generator.EventGenerator;
import net.acesinc.data.json.generator.SimulationRunner;
import net.acesinc.data.json.generator.config.SimulationConfig;
import net.acesinc.data.json.generator.config.WorkflowConfig;
import net.acesinc.data.json.generator.log.EventLogger;
import net.acesinc.data.json.generator.log.KafkaLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamPipesSimulationRunner {

  private static final Logger log = LogManager.getLogger(SimulationRunner.class);
  private SimulationConfig config;
  private List<EventGenerator> eventGenerators;
  private List<Thread> eventGenThreads;
  private boolean running;
  private List<EventLogger> eventLoggers;
  private Map<String, TopicAwareWorkflow> simulationWorkflows;
  private String kafkaHost;
  private Integer kafkaPort;

  public StreamPipesSimulationRunner(SimulationConfig config, Map<String,
      TopicAwareWorkflow>
      simulationWorkflows, String kafkaHost, Integer kafkaPort) {
    this.config = config;
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    eventGenerators = new ArrayList<>();
    eventLoggers = new ArrayList<>();
    eventGenThreads = new ArrayList<>();
    this.simulationWorkflows = simulationWorkflows;

    setupSimulation();
  }

  private Map<String, Object> makeProducerConfig(String topic) {
    Map<String, Object> producerConfig = new HashMap<>();
    producerConfig.put("broker.server", this.kafkaHost);
    producerConfig.put("broker.port", this.kafkaPort);
    producerConfig.put("flatten", false);
    producerConfig.put("sync", false);
    producerConfig.put("topic", topic);

    return producerConfig;
  }

  private void setupSimulation() {
    running = false;
    for (WorkflowConfig workflowConfig : config.getWorkflows()) {
      if (simulationWorkflows.containsKey(workflowConfig.getWorkflowFilename())) {
        TopicAwareWorkflow w = simulationWorkflows.get(workflowConfig.getWorkflowFilename());
        KafkaLogger kafkaLogger = new KafkaLogger(makeProducerConfig(w.getTargetTopic()));
        eventLoggers.add(kafkaLogger);
        final EventGenerator gen = new EventGenerator(w, workflowConfig,
            Collections.singletonList(kafkaLogger));
        log.info("Adding EventGenerator for [ " + workflowConfig.getWorkflowName() + ","
            + workflowConfig.getWorkflowFilename() + " ]");
        eventGenerators.add(gen);
        eventGenThreads.add(new Thread(gen));
      }
    }
  }

  public void startSimulation() {
    log.info("Starting Simulation");

    if (eventGenThreads.size() > 0) {
      for (Thread t : eventGenThreads) {
        t.start();
      }
      running = true;
    }
  }

  public void stopSimulation() {
    log.info("Stopping Simulation");
    for (Thread t : eventGenThreads) {
      t.interrupt();
    }
    for (EventLogger l : eventLoggers) {
      l.shutdown();
    }
    running = false;
  }

  public boolean isRunning() {
    return running;
  }

}
