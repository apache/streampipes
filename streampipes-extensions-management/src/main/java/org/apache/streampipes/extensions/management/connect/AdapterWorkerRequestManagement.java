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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AdapterWorkerRequestManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerRequestManagement.class);

  private final AdapterWorkerManagement adapterManagement;

  public AdapterWorkerRequestManagement() {
    this(AdapterTransitionRegistry.INSTANCE);
  }

  public AdapterWorkerRequestManagement(AdapterTransitionRegistry adapterTransitionRegistry) {
    this(new AdapterWorkerManagement(
        RunningAdapterInstances.INSTANCE,
        DeclarersSingleton.getInstance(),
        adapterTransitionRegistry
    ));
  }

  public AdapterWorkerRequestManagement(AdapterWorkerManagement adapterManagement) {
    this.adapterManagement = adapterManagement;
  }

  public Collection<AdapterDescription> getRunningAdapterInstances() {
    return adapterManagement.getAllRunningAdapterInstances();
  }

  public SuccessMessage invokeAdapter(AdapterDescription adapterStreamDescription) throws AdapterException {
    adapterManagement.invokeAdapter(adapterStreamDescription);
    String responseMessage = "Stream adapter with id "
        + adapterStreamDescription.getElementId()
        + " successfully started";
    LOG.info(responseMessage);
    return Notifications.success(responseMessage);
  }

  public SuccessMessage stopAdapter(AdapterDescription adapterStreamDescription) throws AdapterException {
    String responseMessage;

    if (adapterStreamDescription.isRunning()) {
      adapterManagement.stopAdapter(adapterStreamDescription);
      responseMessage = "Stream adapter with id " + adapterStreamDescription.getElementId() + " successfully stopped";
    } else {
      responseMessage =
          "Stream adapter with id " + adapterStreamDescription.getElementId() + " seems not to be running";
    }

    LOG.info(responseMessage);
    return Notifications.success(responseMessage);
  }
}
