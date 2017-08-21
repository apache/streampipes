package org.streampipes.pe.sinks.standalone.config;


import org.streampipes.config.SpConfig;

public enum ActionConfig {

  INSTANCE;

  private SpConfig config;
  private final static String HOST = "host";
  private final static String PORT = "port";
  private final static String KAFKA_HOST = "kafka_host";
  private final static String KAFKA_PORT = "kafka_port";
  private final static String ZOOKEEPER_HOST = "zookeeper_host";
  private final static String ZOOKEEPER_PORT = "zookeeper_port";
  private final static String COUCHDB_HOST = "couchdb_host";
  private final static String COUCHDB_PORT = "cochdb_port";
  private final static String JMS_HOST = "jms_host";
  private final static String JMS_PORT = "jms_port";
  private final static String SIMULATION_DELAY = "simulation_delay_ms";
  private final static String SIMULATION_MAX_EVENTS = "simulation_max_events";
  private final static String NGINX_HOST = "nginx_host";
  private final static String NGINX_PORT = "nginx_port";


  public final static String serverUrl;
  public final static String iconBaseUrl;

  ActionConfig() {
    config = SpConfig.getSpConfig("pe/org.streampipes.pe.sinks.standalone");

    config.register(HOST, "pe-sinks", "Hostname for the pe sinks");
    config.register(PORT, 8090, "Port for the pe sinks");
    config.register(NGINX_HOST, System.getenv("STREAMPIPES_HOST"), "External hostname of " +
            "StreamPipes Nginx");
    config.register(NGINX_PORT, 80, "External port of StreamPipes Nginx");
    config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
    config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
    config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sinks project");
    config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sinks project");
    config.register(COUCHDB_HOST, "couchdb", "Host for couchdb of the pe sinks project");
    config.register(COUCHDB_PORT, 5984, "Port for couchdb of the pe sinks project");
    config.register(JMS_HOST, "tcp://activemq", "Hostname for pe actions service for active mq");
    config.register(JMS_PORT, 9092, "Port for pe actions service for active mq");
    config.register(SIMULATION_DELAY, 10, "Delay time in milliseconds for the simulation");
    config.register(SIMULATION_MAX_EVENTS, 105000, "Maximal number of events for the simulation");
  }

  static {
    serverUrl = ActionConfig.INSTANCE.getHost() + ":" + ActionConfig.INSTANCE.getPort();
    iconBaseUrl = ActionConfig.INSTANCE.getHost() + ":" + ActionConfig.INSTANCE.getPort() + "/img";
  }

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  public String getHost() {
    return config.getString(HOST);
  }

  public int getPort() {
    return config.getInteger(PORT);
  }

  public String getKafkaHost() {
    return config.getString(KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(KAFKA_PORT);
  }

  public String getKafkaUrl() {
    return getKafkaHost() + ":" + getKafkaPort();
  }

  public String getZookeeperHost() {
    return config.getString(ZOOKEEPER_HOST);
  }

  public int getZookeeperPort() {
    return config.getInteger(ZOOKEEPER_PORT);
  }

  public String getCouchDbHost() {
    return config.getString(COUCHDB_HOST);
  }

  public int getCouchDbPort() {
    return config.getInteger(COUCHDB_PORT);
  }

  public String getJmsHost() {
    return config.getString(JMS_HOST);
  }

  public int getJmsPort() {
    return config.getInteger(JMS_PORT);
  }

  public String getJmsUrl() {
    return getJmsHost() + ":" + getJmsPort();
  }

  public int getSimulationDelay() {
    return config.getInteger(SIMULATION_DELAY);
  }

  public int getSimulationMaxEvents() {
    return config.getInteger(SIMULATION_MAX_EVENTS);
  }

  public String getNginxHost() {
    return config.getString(NGINX_HOST);
  }

  public Integer getNginxPort() {
    return config.getInteger(NGINX_PORT);
  }

}
