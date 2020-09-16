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

package org.apache.streampipes.processors.transformation.flink.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum TransformationFlinkConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;
  public static final String JAR_FILE = "./streampipes-processing-element-container.jar";

  private final static String service_id = "pe/org.apache.streampipes.processors.transformation.flink";
  private final static String service_name = "Processors Transformation Flink";
  private final static String service_container_name = "processors-transformation-flink";

  TransformationFlinkConfig() {
    config = SpConfig.getSpConfig(service_id);

    config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe mixed flink component");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe mixed flink component");
    config.register(ConfigKeys.FLINK_HOST, "jobmanager", "Host for the flink cluster");
    config.register(ConfigKeys.FLINK_PORT, 8081, "Port for the flink cluster");

    config.register(ConfigKeys.DEBUG, false, "When set to true programs are not deployed to cluster, but executed locally");

    config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  public String getFlinkHost() {
    return config.getString(ConfigKeys.FLINK_HOST);
  }

  public int getFlinkPort() {
    return config.getInteger(ConfigKeys.FLINK_PORT);
  }

  public boolean getDebug() {
    return config.getBoolean(ConfigKeys.DEBUG);
  }

  @Override
  public String getId() {
    return service_id;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }
}
