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
package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.commons.environment.Environment;

import org.apache.iotdb.session.pool.SessionPool;

/**
 * This class provides a method to retrieve a session pool for IoT DB operations based on the given environment
 * configuration.
 */
public class IotDbSessionProvider {

  /**
   * Retrieves a session pool for IoT DB operations.
   * <p>
   * The session pool is configured by the StreamPipes environment and respective environment variables.
   *
   * @param environment
   *          the environment configuration containing IoT DB connection details.
   * @return a SessionPool configured based on the provided environment.
   */
  public SessionPool getSessionPool(Environment environment) {
    return new SessionPool.Builder().maxSize(environment.getIotDbSessionPoolSize().getValueOrDefault())
            .enableCompression(environment.getIotDbSessionEnableCompression().getValueOrDefault())
            .host(environment.getTsStorageHost().getValueOrDefault())
            .port(environment.getTsStoragePort().getValueOrDefault())
            .user(environment.getIotDbUser().getValueOrDefault())
            .password(environment.getIotDbPassword().getValueOrDefault()).build();
  }
}
