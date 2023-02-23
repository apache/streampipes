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
import org.apache.streampipes.commons.environment.Environment;

import com.ecwid.consul.v1.ConsulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public enum ConsulProvider {

  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(ConsulProvider.class);
  private static final int CHECK_INTERVAL = 1;

  private ConsulClient consulClient;
  private boolean initialized = false;

  ConsulProvider() {
  }

  public ConsulClient getConsulInstance(Environment environment) {
    if (!initialized) {
      createConsulInstance(environment);
    }

    return consulClient;
  }

  private void createConsulInstance(Environment environment) {
    var consulHost = getConsulHost(environment);
    var consulPort = getConsulPort(environment);
    var connected = false;

    LOG.info("Checking if consul is available on host {} and port {}", consulHost, consulPort);
    connected = checkConsulAvailable(consulHost, consulPort);

    if (connected) {
      LOG.info("Successfully connected to Consul on host {}", consulHost);
      this.consulClient = new ConsulClient(consulHost, consulPort);
      this.initialized = true;
    } else {
      LOG.info("Retrying in {} second", CHECK_INTERVAL);
      try {
        TimeUnit.SECONDS.sleep(CHECK_INTERVAL);
        createConsulInstance(environment);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean checkConsulAvailable(String consulHost,
                                       int consulPort) {
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

  private int getConsulPort(Environment environment) {
    return environment.getConsulPort().getValueOrDefault();
  }

  private String getConsulHost(Environment environment) {
    if (environment.getConsulLocation().exists()) {
      return environment.getConsulLocation().getValue();
    } else {
      if (environment.getSpDebug().getValueOrReturn(false)) {
        return DefaultEnvValues.LOCALHOST;
      } else {
        return environment.getConsulHost().getValueOrDefault();
      }
    }
  }
}
