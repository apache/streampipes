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
package org.apache.streampipes.connect.container.worker.init;

import org.apache.streampipes.connect.container.worker.management.MasterRestClient;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ConnectWorkerRegistrationService {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectWorkerRegistrationService.class);

  public void registerWorker(String serviceGroup) {
    String masterUrl = getConnectMasterUrl() + "/streampipes-backend";
    boolean connected = false;

    while (!connected) {
      LOG.info("Trying to connect to master: " + masterUrl);
      connected = MasterRestClient.register(masterUrl,
              new ConnectWorkerDescriptionProvider().getContainerDescription(serviceGroup));

      if (!connected) {
        LOG.info("Retrying in 5 seconds");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    LOG.info("Successfully connected to master: " + masterUrl + " Worker is now running.");
  }

  private String getConnectMasterUrl() {
    return SpServiceDiscovery
            .getServiceDiscovery()
            .getServiceEndpoints("core", false, Arrays.asList()).get(0);
  }
}
