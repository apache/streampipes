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

package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.connect.management.health.AdapterOperationLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PostStartupRecovery {

  private final ExtensionHealthCheck extensionHealthCheck;

  public PostStartupRecovery(ExtensionHealthCheck extensionHealthCheck) {
    this.extensionHealthCheck = extensionHealthCheck;
  }

  private static final Logger LOG = LoggerFactory.getLogger(PostStartupRecovery.class);

  private static final int MAX_RETRIES = 7;

  public void checkAndRestore(int retryCount) {
    if (AdapterOperationLock.INSTANCE.isLocked()) {
      LOG.info("Adapter operation already in progress, {}/{}", (retryCount + 1), MAX_RETRIES);
      if (retryCount <= MAX_RETRIES) {
        try {
          TimeUnit.MILLISECONDS.sleep(3000);
          retryCount++;
          checkAndRestore(retryCount);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        LOG.info("Max retries for running adapter operations reached, will do unlock which might cause conflicts...");
        AdapterOperationLock.INSTANCE.unlock();
        this.extensionHealthCheck.run();
      }
    } else {
      AdapterOperationLock.INSTANCE.lock();
      this.extensionHealthCheck.run();
      AdapterOperationLock.INSTANCE.unlock();
    }
  }
}
