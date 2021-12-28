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
