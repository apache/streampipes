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
package org.apache.streampipes.manager.setup;

import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpointItem;
import org.apache.streampipes.model.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PipelineElementInstallationStep extends InstallationStep {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementInstallationStep.class);
  private static final int MAX_RETRIES = 8;

  private final ExtensionsServiceEndpoint endpoint;
  private final String principalSid;
  private int retries = 0;


  public PipelineElementInstallationStep(ExtensionsServiceEndpoint endpoint,
                                         String principalSid) {
    this.endpoint = endpoint;
    this.principalSid = principalSid;
  }

  @Override
  public void install() {
    List<Message> statusMessages = new ArrayList<>();
    List<ExtensionsServiceEndpointItem> items = Operations.getEndpointUriContents(Collections.singletonList(endpoint));
    if (items.isEmpty() && retries <= MAX_RETRIES) {
      retries++;
      LOG.info(
          "Endpoint available but no extensions yet found, so we will retry to fetch pipeline elements ({}/{})",
          retries,
          MAX_RETRIES
      );
      try {
        TimeUnit.SECONDS.sleep(2);
        install();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } else {
      LOG.info("Found {} endpoint items for endpoint {}", items.size(), endpoint.getEndpointUrl());
      for (ExtensionsServiceEndpointItem item : items) {
        statusMessages.add(new EndpointItemParser().parseAndAddEndpointItem(item.getUri(),
            principalSid, true));
      }

      if (statusMessages.stream().allMatch(Message::isSuccess)) {
        logSuccess(getTitle());
      } else {
        logFailure(getTitle());
      }
    }

  }

  @Override
  public String getTitle() {
    return "Installing pipeline elements from " + endpoint.getEndpointUrl();
  }
}
