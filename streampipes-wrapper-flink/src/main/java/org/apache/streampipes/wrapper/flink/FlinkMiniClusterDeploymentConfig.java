package org.apache.streampipes.wrapper.flink;

public class FlinkMiniClusterDeploymentConfig extends FlinkDeploymentConfig {

    public FlinkMiniClusterDeploymentConfig() {
        super("", "localhost", 6123, true);
    }
}
