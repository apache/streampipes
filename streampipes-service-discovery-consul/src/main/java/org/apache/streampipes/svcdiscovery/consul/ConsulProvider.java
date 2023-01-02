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
package org.apache.streampipes.svcdiscovery.consul;

import org.apache.streampipes.commons.constants.DefaultEnvValues;
import org.apache.streampipes.commons.constants.Envs;

import com.orbitz.consul.Consul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ConsulProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConsulProvider.class);
  private static final int CHECK_INTERVAL = 1;

  private final String consulHost;
  private final String consulUrlString;
  private final int consulPort;

  public ConsulProvider() {
    this.consulHost = getConsulHost();
    this.consulPort = getConsulPort();
    this.consulUrlString = makeConsulUrl();
  }

  public Consul consulInstance() {
    boolean connected;

    do {
      LOG.info("Checking if consul is available on host {} and port {}", consulHost, consulPort);
      connected = checkConsulAvailable();

      if (!connected) {
        LOG.info("Retrying in {} second", CHECK_INTERVAL);
        try {
          TimeUnit.SECONDS.sleep(CHECK_INTERVAL);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while (!connected);

    LOG.info("Successfully connected to Consul");
    return Consul.builder().withUrl(consulUrlString).build();
  }

  public String makeConsulUrl() {
    return "http://" + consulHost + ":" + consulPort;
  }

  private boolean checkConsulAvailable() {
    try {
      InetSocketAddress sa = new InetSocketAddress(consulHost, consulPort);
      Socket ss = new Socket();
      ss.connect(sa, 1000);
      ss.close();

      return true;
    } catch (IOException e) {
      LOG.info("Could not connect to Consul instance...");
      return false;
    }
  }

  private int getConsulPort() {
    return Envs.SP_CONSUL_PORT.getValueAsIntOrDefault(DefaultEnvValues.CONSUL_PORT_DEFAULT);
  }

  private String getConsulHost() {
    if (Envs.SP_CONSUL_LOCATION.exists()) {
      return Envs.SP_CONSUL_LOCATION.getValue();
    } else {
      if (Envs.SP_DEBUG.getValueAsBooleanOrDefault(false)) {
        return DefaultEnvValues.CONSUL_HOST_LOCAL;
      } else {
        return Envs.SP_CONSUL_HOST.getValueOrDefault(DefaultEnvValues.CONSUL_HOST_DEFAULT);
      }
    }
  }
}
