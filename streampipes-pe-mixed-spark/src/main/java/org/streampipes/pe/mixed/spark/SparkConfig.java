package org.streampipes.pe.mixed.spark;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public enum SparkConfig implements PeConfig {
    INSTANCE;

    private SpConfig config;
    public static final String JAR_FILE = "./streampipes-spark-test1.jar";

    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String KAFKA_HOST = "kafka_host";
    private final static String KAFKA_PORT = "kafka_port";
    private final static String SPARK_HOST = "spark_host";
    private final static String SPARK_DURATION = "spark_duration";

    private final static String SERVICE_ID = "pe/org.streampipes.biggis.pe.spark.test1";
    private final static String SERVICE_NAME = "service_name";
    private final static String appName = "Spark-Test-1b";

    SparkConfig() {
        config = SpConfig.getSpConfig("pe/org.streampipes.biggis.pe.spark.test1");

        config.register(HOST, "pe-spark-test1", "Hostname for the pe spark test1 component");
        config.register(PORT, 8090, "Port for the pe spark test1 component");
        config.register(SPARK_HOST, "jobmanager", "Host for the spark cluster");
        config.register(SPARK_DURATION, 1000, "Spark micro batch duration (in ms)");
        config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
        config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");

        config.register(SERVICE_NAME, appName, "The name of the service");
    }

    public String getHost() {
        return config.getString(HOST);
    }

    public int getPort() {
        return config.getInteger(PORT);
    }

    public String getSparkHost() {
        return config.getString(SPARK_HOST);
    }

    public long getSparkBatchDuration() {
        return config.getInteger(SPARK_DURATION);
    }

    public String getKafkaHost() {
        return config.getString(KAFKA_HOST);
    }

    public int getKafkaPort() {
        return config.getInteger(KAFKA_PORT);
    }

    public static final String iconBaseUrl = SparkConfig.INSTANCE.getHost() + "/img";

    public static final String getIconUrl(String pictureName) {
        return iconBaseUrl + "/" + pictureName + ".png";
    }

    @Override
    public String getId() {
        return SERVICE_ID;
    }

    @Override
    public String getName() {
        return config.getString(SERVICE_NAME);
    }

    public String getAppName() {
        return appName;
    }

    public String getKafkaHostPort() {
        return getKafkaHost() + ":" + getKafkaPort();
    }
}
