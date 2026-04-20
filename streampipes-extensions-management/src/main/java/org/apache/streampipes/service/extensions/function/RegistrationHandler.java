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

package org.apache.streampipes.service.extensions.function;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.function.FunctionDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class RegistrationHandler implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(FunctionRegistrationHandler.class);
  private static final int MAX_RETRIES = 3;
  private static final long WAIT_TIME_BETWEEN_RETRIES = 5000;

  protected final List<FunctionDefinition> functions;

  public RegistrationHandler(List<FunctionDefinition> functions) {
    this.functions = functions;
  }

  public void run() {
    initRequest(0);
  }

  public void initRequest(int retryCount) {
    try {
      var client = getStreamPipesClient();
      performRequest(client);
      logSuccess();
    } catch (Exception e) {
      if (retryCount < MAX_RETRIES) {
        LOG.warn("Could not execute registration request", e);
        try {
          TimeUnit.MILLISECONDS.sleep(WAIT_TIME_BETWEEN_RETRIES);
        } catch (InterruptedException ex) {
          LOG.error("Error while initiating sleep", ex);
        }
        retryCount++;
        initRequest(retryCount);
      }
    }
  }

  protected abstract void performRequest(StreamPipesClient client);

  protected abstract void logSuccess();

  private StreamPipesClient getStreamPipesClient() {
    return new StreamPipesClientResolver().makeStreamPipesClientInstance();
  }
}
