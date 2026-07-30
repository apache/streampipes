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

package org.apache.streampipes.extensions.connectors.opcua.config.security;

import org.eclipse.milo.opcua.stack.core.security.FileBasedTrustListManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class SharedTrustListManagerProvider implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(SharedTrustListManagerProvider.class);

  private final TrustListManagerFactory factory;
  private final Map<Path, FileBasedTrustListManager> trustListManagers;

  SharedTrustListManagerProvider(TrustListManagerFactory factory) {
    this.factory = factory;
    this.trustListManagers = new HashMap<>();
  }

  static SharedTrustListManagerProvider getInstance() {
    return InstanceHolder.INSTANCE;
  }

  synchronized FileBasedTrustListManager getOrCreate(Path trustListDirectory) throws IOException {
    var normalizedDirectory = trustListDirectory.toAbsolutePath().normalize();
    var trustListManager = trustListManagers.get(normalizedDirectory);

    if (trustListManager == null) {
      trustListManager = factory.create(normalizedDirectory);
      trustListManagers.put(normalizedDirectory, trustListManager);
    }

    return trustListManager;
  }

  @Override
  public synchronized void close() throws IOException {
    IOException closeException = null;

    for (var trustListManager : trustListManagers.values()) {
      try {
        trustListManager.close();
      } catch (IOException e) {
        if (closeException == null) {
          closeException = e;
        } else {
          closeException.addSuppressed(e);
        }
      }
    }
    trustListManagers.clear();

    if (closeException != null) {
      throw closeException;
    }
  }

  @FunctionalInterface
  interface TrustListManagerFactory {

    FileBasedTrustListManager create(Path trustListDirectory) throws IOException;
  }

  private static final class InstanceHolder {

    private static final SharedTrustListManagerProvider INSTANCE = createInstance();

    private static SharedTrustListManagerProvider createInstance() {
      var provider = new SharedTrustListManagerProvider(FileBasedTrustListManager::createAndInitialize);
      Runtime.getRuntime().addShutdownHook(
          new Thread(() -> closeOnShutdown(provider), "opcua-trust-list-manager-shutdown")
      );
      return provider;
    }

    private static void closeOnShutdown(SharedTrustListManagerProvider provider) {
      try {
        provider.close();
      } catch (IOException e) {
        LOG.warn("Could not close the shared OPC UA trust list manager", e);
      }
    }
  }
}
