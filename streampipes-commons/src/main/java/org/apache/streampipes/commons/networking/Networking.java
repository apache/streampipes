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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Networking {

  private static final Logger LOG = LoggerFactory.getLogger(Networking.class);

  private static final String DEFAULT_LOCALHOST_IP = "127.0.0.1";

  public static String getHostname() throws UnknownHostException {
    String selectedAddress;
    if (Envs.SP_HOST.exists()) {
      selectedAddress = Envs.SP_HOST.getValue();
      LOG.info("Using IP from provided environment variable {}: {}", Envs.SP_HOST, selectedAddress);
    } else {
      selectedAddress = InetAddress.getLocalHost().getHostAddress();

      // this condition is only used as a workaround when the IP address was not derived correctly
      // when this also does not work, you must set the environment variable SP_HOST manually
      if (selectedAddress.equals(DEFAULT_LOCALHOST_IP)) {
        selectedAddress = getIpAddressForOsx();
      }

      LOG.info("Using auto-discovered IP: {}", selectedAddress);
    }

    return selectedAddress;
  }

  /**
   * this method is a workaround for developers using osx
   * in OSX InetAddress.getLocalHost().getHostAddress() always returns 127.0.0.1
   * as a workaround developers must manually set the SP_HOST environment variable with the actual ip
   * with this method the IP is set automatically
   *
   * @return IP
   */
  private static String getIpAddressForOsx() {

    Socket socket = new Socket();
    String result = DEFAULT_LOCALHOST_IP;
    try {
      socket.connect(new InetSocketAddress("streampipes.apache.org", 80));
      result = socket.getLocalAddress().getHostAddress();
      socket.close();
    } catch (IOException e) {
      LOG.error(e.getMessage());
      LOG.error("IP address was not set automatically. Use the environment variable SP_HOST to set it manually.");
    }

    return result;
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
