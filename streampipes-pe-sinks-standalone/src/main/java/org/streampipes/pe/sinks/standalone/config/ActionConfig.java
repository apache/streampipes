package org.streampipes.pe.sinks.standalone.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum ActionConfig implements PeConfig {

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
  private final static String ICON_HOST = "icon_host";
  private final static String ICON_PORT = "icon_port";


  public final static String serverUrl;
  public final static String iconBaseUrl;


  private final static String SERVICE_ID= "pe/org.streampipes.pe.sinks.standalone";
  private final static String SERVICE_NAME = "service_name";

  ActionConfig() {
    config = SpConfig.getSpConfig(SERVICE_ID);

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
    config.register(JMS_PORT, 61616, "Port for pe actions service for active mq");
    config.register(SIMULATION_DELAY, 10, "Delay time in milliseconds for the simulation");
    config.register(SIMULATION_MAX_EVENTS, 105000, "Maximal number of events for the simulation");

    config.register(ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ICON_PORT, 80, "Port for the icons in nginx");

    config.register(SERVICE_NAME, "Sinks standalone", "The name of the service");

  }

  static {
    serverUrl = ActionConfig.INSTANCE.getHost() + ":" + ActionConfig.INSTANCE.getPort();
    iconBaseUrl = ActionConfig.INSTANCE.getIconHost() + ":" + ActionConfig.INSTANCE.getIconPort() + "/img/pe_icons";
  }

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  @Override
  public String getHost() {
    return config.getString(HOST);
  }

  @Override
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

  public String getIconHost() {
    return config.getString(ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ICON_PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(SERVICE_NAME);
  }

}
