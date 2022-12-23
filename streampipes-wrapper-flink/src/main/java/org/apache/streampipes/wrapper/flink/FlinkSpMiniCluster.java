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

import org.apache.flink.client.program.MiniClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;

public enum FlinkSpMiniCluster {

  INSTANCE;

  private MiniCluster miniCluster;
  private boolean miniClusterRunning;

  FlinkSpMiniCluster() {
    Configuration configuration = new Configuration();
    configuration.setString(RestOptions.BIND_PORT, "0");
    MiniClusterConfiguration miniClusterConfiguration = new MiniClusterConfiguration
        .Builder()
        .setConfiguration(configuration)
        .setNumTaskManagers(2)
        .build();
    this.miniCluster = new MiniCluster(miniClusterConfiguration);
  }

  public void start() throws Exception {
    if (!this.miniClusterRunning) {
      this.miniCluster.start();
      this.miniClusterRunning = true;
    }
  }

  public MiniClusterClient getClusterClient() {
    return new MiniClusterClient(new Configuration(), miniCluster);
  }

}
