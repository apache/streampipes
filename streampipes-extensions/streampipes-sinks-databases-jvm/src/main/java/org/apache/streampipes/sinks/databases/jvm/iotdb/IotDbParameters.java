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

package org.apache.streampipes.sinks.databases.jvm.iotdb;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class IotDbParameters extends EventSinkBindingParams {

  private final String host;
  private final Integer port;

  private final String user;
  private final String password;

  private final String database;
  // entity
  private final String device;

  private final String timestampField;

  public IotDbParameters(DataSinkInvocation graph,
                         String host,
                         Integer port,
                         String user,
                         String password,
                         String database,
                         String device,
                         String timestampField) {
    super(graph);

    this.host = host;
    this.port = port;

    this.user = user;
    this.password = password;

    this.database = database;
    this.device = device;

    this.timestampField = timestampField;
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getDatabase() {
    return database;
  }

  public String getDevice() {
    return device;
  }

  public String getTimestampField() {
    return timestampField;
  }
}
