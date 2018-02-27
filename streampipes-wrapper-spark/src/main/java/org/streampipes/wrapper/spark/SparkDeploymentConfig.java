package org.streampipes.wrapper.spark;

import java.io.Serializable;

/**
 * Created by Jochen Lutz on 2018-01-18.
 */
public class SparkDeploymentConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jarFile;
    private String appName;
    private String sparkHost;
    private long sparkBatchDuration;
    private String kafkaHost;
    private boolean runLocal;

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, boolean runLocal, long sparkBatchDuration, String kafkaHost) {
        super();

        this.jarFile = jarFile;
        this.appName = appName;
        this.sparkHost = sparkHost;
        this.runLocal = runLocal;
        this.sparkBatchDuration = sparkBatchDuration;
        this.kafkaHost = kafkaHost;//TODO: JMS berücksichtigen

    }

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, long sparkBatchDuration, String kafkaHost) {
        this(jarFile, appName, sparkHost, false, sparkBatchDuration, kafkaHost);
    }

    public SparkDeploymentConfig(String jarFile, String appName, String sparkHost, String kafkaHost) {
        this(jarFile, appName, sparkHost, 1000, kafkaHost);
    }

    public String getJarFile() {
        return jarFile;
    }

    public void setJarFile(String jarFile) {
        this.jarFile = jarFile;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSparkHost() {
        return sparkHost;
    }

    public void setSparkHost(String sparkHost) {
        this.sparkHost = sparkHost;
    }

    public boolean isRunLocal() {
        return runLocal;
    }

    public void setRunLocal(boolean runLocal) {
        this.runLocal = runLocal;
    }

    public long getSparkBatchDuration() {
        return sparkBatchDuration;
    }

    public void setSparkBatchDuration(long sparkBatchDuration) {
        this.sparkBatchDuration = sparkBatchDuration;
    }

    public String getKafkaHost() {
        return kafkaHost;
    }

    public void setKafkaHost(String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }
}
