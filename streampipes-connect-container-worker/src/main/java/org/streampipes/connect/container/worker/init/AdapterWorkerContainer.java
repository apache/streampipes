/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.connect.container.worker.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.container.worker.management.MasterRestClient;
import org.streampipes.connect.init.AdapterDeclarerSingleton;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.worker.ConnectWorkerContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public abstract class AdapterWorkerContainer {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerContainer.class);

  public void init(String workerUrl, String masterUrl, Integer workerPort) {

    LOG.info("Started StreamPipes Connect Resource in WORKER mode");

    SpringApplication app = new SpringApplication(AdapterWorkerContainer.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", workerPort));
    app.run();


    boolean connected = false;

    while (!connected) {
      LOG.info("Trying to connect to master: " + masterUrl);
      connected = MasterRestClient.register(masterUrl, getContainerDescription(workerUrl));

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

  private ConnectWorkerContainer getContainerDescription(String endpointUrl) {

    List<AdapterDescription> adapters = new ArrayList<>();
    for (Adapter a : AdapterDeclarerSingleton.getInstance().getAllAdapters()) {
      adapters.add(a.declareModel());
    }

    List<ProtocolDescription> protocols = new ArrayList<>();
    for (Protocol p : AdapterDeclarerSingleton.getInstance().getAllProtocols()) {
      protocols.add(p.declareModel());
    }

    ConnectWorkerContainer result = new ConnectWorkerContainer(endpointUrl, protocols, adapters);
    return result;
  }
}
