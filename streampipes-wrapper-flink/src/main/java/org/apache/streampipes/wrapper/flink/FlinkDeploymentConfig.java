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

package org.apache.streampipes.wrapper.flink;

import java.io.Serializable;

public class FlinkDeploymentConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private String jarFile;
  private String host;
  private int port;
  private boolean miniClusterMode;

  public FlinkDeploymentConfig(String jarFile,
                               String host,
                               int port) {
    super();
    this.jarFile = jarFile;
    this.host = host;
    this.port = port;
  }

  public FlinkDeploymentConfig(String jarFile,
                               String host,
                               int port,
                               boolean miniClusterMode) {
    this(jarFile, host, port);
    this.miniClusterMode = miniClusterMode;
  }

  public String getJarFile() {
    return jarFile;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public boolean isMiniClusterMode() {
    return miniClusterMode;
  }

}
