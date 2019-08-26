/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.iotdb;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class IotDbParameters extends EventSinkBindingParams {

  private String iotDbHost;
  private Integer iotdbPort;
  private String dbStorageGroup;
  private String user;
  private String password;

  public IotDbParameters(DataSinkInvocation graph, String iotDbHost, Integer iotdbPort, String dbStorageGroup, String user, String password) {
    super(graph);
    this.iotDbHost = iotDbHost;
    this.iotdbPort = iotdbPort;
    this.dbStorageGroup = dbStorageGroup;
    this.user = user;
    this.password = password;
  }

  public String getIotDbHost() {
    return iotDbHost;
  }

  public Integer getIotDbPort() {
    return iotdbPort;
  }

  public String getDbStorageGroup() {
    return dbStorageGroup;
  }

  public String getUsername() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
