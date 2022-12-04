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

import org.apache.streampipes.commons.networking.Networking;

import java.net.UnknownHostException;

public class BaseNetworkingConfig {

  private final String host;
  private final Integer port;

  public BaseNetworkingConfig(String host,
                              Integer port) {
    this.host = host;
    this.port = port;
  }

  public static BaseNetworkingConfig defaultResolution(Integer defaultPort) throws UnknownHostException {
    String host = Networking.getHostname();
    Integer port = Networking.getPort(defaultPort);

    return new BaseNetworkingConfig(host, port);
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }
}
