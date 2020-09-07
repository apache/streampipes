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
package org.apache.streampipes.pe.flink.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum Config implements PeConfig {
  INSTANCE;

  private final SpConfig config;
  public static final String JAR_FILE = "./streampipes-processing-element-container.jar";
  private final static String SERVICE_ID = "pe/org.apache.streampipes.processors.all.flink";
  private final static String SERVICE_NAME = "Processors Flink (Bundle)";
  private final static String SERVICE_CONTAINER_NAME = "pipeline-elements-all-flink";

  Config() {
    config = SpConfig.getSpConfig(SERVICE_ID);
    config.register(ConfigKeys.HOST, SERVICE_CONTAINER_NAME, "Data processor host");
    config.register(ConfigKeys.PORT, 8090, "Data processor port");
    config.register(ConfigKeys.SERVICE_NAME, SERVICE_NAME, "Data processor service name");
    config.register(ConfigKeys.FLINK_HOST, "jobmanager", "Flink jobmanager host");
    config.register(ConfigKeys.FLINK_PORT, 8081, "Flink jobmanager port");
    config.register(ConfigKeys.FLINK_DEBUG, false, "When set to true programs are not deployed to cluster, but executed locally");
  }

  public String getFlinkHost() {
    return config.getString(ConfigKeys.FLINK_HOST);
  }

  public int getFlinkPort() {
    return config.getInteger(ConfigKeys.FLINK_PORT);
  }

  public boolean getFlinkDebug() {
    return config.getBoolean(ConfigKeys.FLINK_DEBUG);
  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }


}
