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

import org.apache.streampipes.commons.constants.Envs;

import com.orbitz.consul.Consul;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class ConsulProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ConsulProvider.class);

  private static final int CONSUL_DEFAULT_PORT = 8500;
  private static final String CONSUL_URL_REGISTER_SERVICE = "v1/agent/service/register";

  public Consul consulInstance() {
    URL consulUrl = consulURL();
    boolean connected;

    do {
      LOG.info("Checking if consul is available...");
      connected = checkConsulAvailable(consulUrl);

      if (!connected) {
        LOG.info("Retrying in 1 second");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while (!connected);

    LOG.info("Successfully connected to Consul");
    return Consul.builder().withUrl(consulURL()).build();
  }

  private URL consulURL() {
    URL url = null;

    if (Envs.SP_CONSUL_LOCATION.exists()) {
      try {
        url = new URL("http", Envs.SP_CONSUL_LOCATION.getValue(), CONSUL_DEFAULT_PORT, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    } else {
      try {
        url = new URL("http", "localhost", CONSUL_DEFAULT_PORT, "");
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return url;
  }

  private boolean checkConsulAvailable(URL consulUrl) {
    try {
      InetSocketAddress sa = new InetSocketAddress(consulUrl.getHost(), consulUrl.getPort());
      Socket ss = new Socket();
      ss.connect(sa, 1000);
      ss.close();

      return true;
    } catch (IOException e) {
      LOG.info("Could not connect to Consul instance...");
      return false;
    }


  }

  public String makeConsulEndpoint() {
    return consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE;
  }
}
