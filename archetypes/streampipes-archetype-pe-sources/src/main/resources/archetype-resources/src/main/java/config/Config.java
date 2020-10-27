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
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
#set( $svc_name = $package.getClass().forName("org.apache.velocity.util.StringUtils").sub("$artifactId", "-", " ") )

package ${package}.config;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum Config implements PeConfig {
  INSTANCE;

  private SpConfig config;
  private final static String SERVICE_ID = "pe/${package}.source";

  Config() {
    config = SpConfig.getSpConfig(SERVICE_ID);
    config.register(ConfigKeys.HOST, "${artifactId}", "Data source host");
    config.register(ConfigKeys.PORT, 8090, "Data source port");
    config.register(ConfigKeys.SERVICE_NAME, "${svc_name}", "Data source service name");
    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Kafka host");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Kafka port");
  }

  public String getKafkaHost() {
    return config.getString(ConfigKeys.KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(ConfigKeys.KAFKA_PORT);
  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }

}
