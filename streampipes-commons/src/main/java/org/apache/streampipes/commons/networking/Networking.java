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
package org.apache.streampipes.commons.networking;

import org.apache.streampipes.commons.constants.Envs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Networking {

  private static final Logger LOG = LoggerFactory.getLogger(Networking.class);

  public static String getHostname() throws UnknownHostException {
    String selectedAddress;
    if (Envs.SP_HOST.exists()) {
      selectedAddress = Envs.SP_HOST.getValue();
      LOG.info("Using IP from provided environment variable {}: {}", Envs.SP_HOST, selectedAddress);
    } else {
      selectedAddress = InetAddress.getLocalHost().getHostAddress();
      LOG.info("Using auto-discovered IP: {}", selectedAddress);
    }

    return selectedAddress;
  }

  public static Integer getPort(Integer defaultPort) {
    Integer selectedPort;
    if (Envs.SP_PORT.exists()) {
      selectedPort = Envs.SP_PORT.getValueAsInt();
      LOG.info("Using port from provided environment variable {}: {}", Envs.SP_PORT, selectedPort);
    } else {
      selectedPort = defaultPort;
      LOG.info("Using default port: {}", defaultPort);
    }

    return selectedPort;
  }
}
