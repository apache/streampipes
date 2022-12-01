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
package org.apache.streampipes.messaging.nats;

import org.apache.streampipes.model.nats.NatsConfig;

import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class NatsUtils {

  private static final Logger LOG = LoggerFactory.getLogger(NatsUtils.class);

  public static Options makeNatsOptions(NatsConfig natsConfig) {
    String natsUrls = natsConfig.getNatsUrls();
    String propertiesAsString = natsConfig.getProperties();
    String username = natsConfig.getUsername();
    String password = natsConfig.getPassword();

    Properties props = new Properties();

    if (username != null) {
      props.setProperty(Options.PROP_USERNAME, username);
      props.setProperty(Options.PROP_PASSWORD, password);
    }

    if (propertiesAsString != null && !propertiesAsString.isEmpty()) {
      splitNatsProperties(propertiesAsString, props);
    }

    String[] natsServerUrls = natsUrls.split(",");
    Options options;
    if (natsServerUrls.length > 1) {
      options = new Options.Builder(props).servers(natsServerUrls).build();
    } else {
      options = new Options.Builder(props).server(natsUrls).build();
    }

    return options;
  }

  private static void splitNatsProperties(String propertiesAsString,
                                          Properties properties) {

    String[] optionalProperties = propertiesAsString.split(",");
    if (optionalProperties.length > 0) {
      for (String header : optionalProperties) {
        try {
          String[] configPropertyWithValue = header.split(":", 2);
          properties.setProperty(configPropertyWithValue[0].trim(), configPropertyWithValue[1].trim());
        } catch (Exception e) {
          LOG.warn("Optional property '" + header + "' is not defined in the correct format.");
        }
      }
    }
  }
}
