/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
 *
 */
package org.streampipes.performance.simulation;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.performance.model.PerformanceTestSettings;
import org.streampipes.performance.producer.DataSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationManager {

  private static final String kafkaUrl = "kafka:9092";

  private PerformanceTestSettings settings;
  private SimulationStatusNotifier notifier;

  private Map<String, Boolean> statusMap;

  public SimulationManager(PerformanceTestSettings settings, SimulationStatusNotifier notifier) {
    this.settings = settings;
    this.notifier = notifier;
    this.statusMap = new HashMap<>();
  }

  public void initSimulation() {

    List<Thread> threads = new ArrayList<>();

    for(Integer i = 0; i < settings.getNumProducerThreads(); i++) {
      String threadId = RandomStringUtils.randomAlphanumeric(6);
      statusMap.put(threadId, false);
      threads.add(new Thread(new DataSimulator(kafkaUrl, settings.getTotalNumberofEvents(), settings
              .getWaitTimeBetweenEventsInMs(), threadId, threadId1 -> {
                statusMap.put(threadId1, true);
                checkFinished();
              })));
    }

    for(Thread thread : threads) {
      thread.start();
    }
  }

  private void checkFinished() {
    if (statusMap.keySet().stream().allMatch(key -> statusMap.get(key))) {
     notifier.onFinished();
    }
  }
}
