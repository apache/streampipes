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
package org.apache.streampipes.service.extensions.connect;

import org.apache.streampipes.extensions.management.connect.ConnectWorkerDescriptionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectWorkerRegistrationService {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerRegistrationService.class);

  public void registerWorker() {
    var connected = false;

    while (!connected) {
      connected = ConnectRestClient.register(
          new ConnectWorkerDescriptionProvider().getAdapterDescriptions());

      if (connected) {
        LOG.info("Successfully connected to master. Worker is now running.");
      } else {
        LOG.info("Retrying in 5 seconds");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
