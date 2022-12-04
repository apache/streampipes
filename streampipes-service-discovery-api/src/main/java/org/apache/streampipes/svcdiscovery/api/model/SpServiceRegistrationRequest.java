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
package org.apache.streampipes.svcdiscovery.api.model;

import java.util.List;

public class SpServiceRegistrationRequest {

  private String svcGroup;
  private String svcId;
  private String host;
  private int port;
  private List<SpServiceTag> tags;
  private String healthCheckPath;

  public SpServiceRegistrationRequest(String svcGroup,
                                      String svcId,
                                      String host,
                                      int port,
                                      List<SpServiceTag> tags,
                                      String healthCheckPath) {
    this.svcGroup = svcGroup;
    this.svcId = svcId;
    this.host = host;
    this.port = port;
    this.tags = tags;
    this.healthCheckPath = healthCheckPath;
  }

  public static SpServiceRegistrationRequest from(String svcGroup,
                                                  String svcId,
                                                  String host,
                                                  Integer port,
                                                  List<SpServiceTag> tags) {
    return new SpServiceRegistrationRequest(svcGroup, svcId, host, port, tags, "");
  }

  public static SpServiceRegistrationRequest from(String svcGroup,
                                                  String svcId,
                                                  String host,
                                                  Integer port,
                                                  List<SpServiceTag> tags,
                                                  String healthCheckPath) {
    return new SpServiceRegistrationRequest(svcGroup, svcId, host, port, tags, healthCheckPath);
  }

  public String getSvcGroup() {
    return svcGroup;
  }

  public void setSvcGroup(String svcGroup) {
    this.svcGroup = svcGroup;
  }

  public String getSvcId() {
    return svcId;
  }

  public void setSvcId(String svcId) {
    this.svcId = svcId;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public List<SpServiceTag> getTags() {
    return tags;
  }

  public void setTags(List<SpServiceTag> tags) {
    this.tags = tags;
  }

  public String getHealthCheckPath() {
    return healthCheckPath;
  }

  public void setHealthCheckPath(String healthCheckPath) {
    this.healthCheckPath = healthCheckPath;
  }
}
