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

import com.orbitz.consul.Consul;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ConsulProvider {

  private static final String CONSUL_ENV_LOCATION = "CONSUL_LOCATION";
  private static final int CONSUL_DEFAULT_PORT = 8500;
  private static final String CONSUL_URL_REGISTER_SERVICE = "v1/agent/service/register";

  public Consul consulInstance() {
    return Consul.builder().withUrl(consulURL()).build();
  }

  private URL consulURL() {
    Map<String, String> env = System.getenv();
    URL url = null;

    if (env.containsKey(CONSUL_ENV_LOCATION)) {
      try {
        url = new URL("http", env.get(CONSUL_ENV_LOCATION), CONSUL_DEFAULT_PORT, "");
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

  public String makeConsulEndpoint() {
    return consulURL().toString() + "/" + CONSUL_URL_REGISTER_SERVICE;
  }
}
