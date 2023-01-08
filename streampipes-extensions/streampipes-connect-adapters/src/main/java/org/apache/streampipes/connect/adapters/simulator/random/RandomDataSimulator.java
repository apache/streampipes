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
package org.apache.streampipes.connect.adapters.simulator.random;

import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomDataSimulator implements Runnable {

  private AdapterPipeline adapterPipeline;

  private Random random;
  private Boolean running;
  private Boolean infinite;
  private Integer maxEvents;
  private Integer waitTimeMs;

  public RandomDataSimulator(AdapterPipeline adapterPipeline, Integer waitTimeMs,
                             Integer maxEvents) {
    this(adapterPipeline, waitTimeMs);
    this.infinite = false;
    this.maxEvents = maxEvents;

  }

  public RandomDataSimulator(AdapterPipeline adapterPipeline, Integer waitTimeMs) {
    this.running = true;
    this.waitTimeMs = waitTimeMs;
    this.adapterPipeline = adapterPipeline;
    this.random = new Random();
    this.infinite = true;
  }

  @Override
  public void run() {
    int counter = 0;
    this.running = true;
    while ((running && infinite) || (running && counter <= maxEvents)) {
      Map<String, Object> event = buildEvent(System.currentTimeMillis(), counter);
      adapterPipeline.process(event);
      counter++;
      try {
        Thread.sleep(waitTimeMs);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Map<String, Object> buildEvent(long timestamp, int counter) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", timestamp);
    event.put("randomNumber", random.nextInt(100));
    event.put("randomText", randomString());
    event.put("count", counter);
    return event;
  }

  private String randomString() {
    String[] randomStrings = new String[]{"a", "b", "c", "d"};
    Random random = new Random();
    return randomStrings[random.nextInt(3)];
  }

  public void setRunning(Boolean running) {
    this.running = running;
  }
}
