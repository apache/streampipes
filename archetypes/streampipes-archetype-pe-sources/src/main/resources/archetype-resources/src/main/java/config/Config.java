#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;


import ${groupId}.config.SpConfig;

public enum ActionConfig {

  INSTANCE;

  private SpConfig config;
  private final static String HOST = "host";
  private final static String PORT = "port";
  private final static String KAFKA_HOST = "kafka_host";
  private final static String KAFKA_PORT = "kafka_port";
  private final static String ZOOKEEPER_HOST = "zookeeper_host";
  private final static String ZOOKEEPER_PORT = "zookeeper_port";
  private final static String JMS_HOST = "jms_host";
  private final static String JMS_PORT = "jms_port";
  private final static String NGINX_HOST = "nginx_host";
  private final static String NGINX_PORT = "nginx_port";
  private final static String ICON_HOST = "icon_host";
  private final static String ICON_PORT = "icon_port";


  public final static String serverUrl;
  public final static String iconBaseUrl;

  ActionConfig() {
    config = SpConfig.getSpConfig("pe/${package}");

    config.register(HOST, "${projectName}", "Hostname for the pe sinks");
    config.register(PORT, 8090, "Port for the pe sinks");
    config.register(NGINX_HOST, System.getenv("STREAMPIPES_HOST"), "External hostname of " +
            "StreamPipes Nginx");
    config.register(NGINX_PORT, 80, "External port of StreamPipes Nginx");
    config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
    config.register(KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
    config.register(ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sinks project");
    config.register(ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sinks project");
    config.register(JMS_HOST, "activemq", "Hostname for pe actions service for active mq");
    config.register(JMS_PORT, 61616, "Port for pe actions service for active mq");
    config.register(ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ICON_PORT, 80, "Port for the icons in nginx");
  }

  static {
    serverUrl = ActionConfig.INSTANCE.getHost() + ":" + ActionConfig.INSTANCE.getPort();
    iconBaseUrl = "http://" + ActionConfig.INSTANCE.getIconHost() + ":" + ActionConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";
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
    return "tcp://" + config.getInteger(ZOOKEEPER_PORT);
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

}
