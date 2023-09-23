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
package org.apache.streampipes.service.base;


import org.apache.streampipes.commons.environment.Environments;

import java.util.Properties;

public class ServiceBaseConfig {

  private static final String ENDPOINT_INCLUDE_KEY = "management.endpoints.web.exposure.include";

  private static final String ENDPOINTS_ENABLED_BY_DEFAULT = "management.endpoints.enabled-by-default";

  private static final String SERVER_PORT_KEY = "server.port";

  public static void addPrometheusConfig(Properties properties) {

    properties.setProperty(ENDPOINTS_ENABLED_BY_DEFAULT, Environments
                                                .getEnvironment()
                                                .getSetupPrometheusEndpoint()
                                                .getValueOrDefault()
                                                .toString());

    properties.setProperty(ENDPOINT_INCLUDE_KEY, Environments
                                                  .getEnvironment()
                                                  .getPrometheusEndpointInclude()
                                                  .getValueOrDefault());
  }

  public static void addPortConfig(Integer port, Properties properties) {
    properties.setProperty(SERVER_PORT_KEY, port.toString());
  }

}
