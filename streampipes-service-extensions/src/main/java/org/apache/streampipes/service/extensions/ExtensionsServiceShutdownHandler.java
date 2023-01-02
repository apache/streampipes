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
package org.apache.streampipes.service.extensions;

import org.apache.streampipes.extensions.management.init.RunningInstances;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsServiceShutdownHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceShutdownHandler.class);


  public void onShutdown() {
    LOG.info("Shutting down StreamPipes extensions service...");
    int runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();

    while (runningInstancesCount > 0) {
      LOG.info("Waiting for {} running pipeline elements to be stopped...", runningInstancesCount);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOG.error("Could not pause current thread...");
      }
      runningInstancesCount = RunningInstances.INSTANCE.getRunningInstancesCount();
    }
  }
}
