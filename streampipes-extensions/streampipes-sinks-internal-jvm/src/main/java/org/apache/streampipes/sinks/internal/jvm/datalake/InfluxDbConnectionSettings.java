package org.apache.streampipes.sinks.internal.jvm.datalake;

public class InfluxDbConnectionSettings {

  private final Integer influxDbPort;
  private final String influxDbHost;
  private final String databaseName;
  private final String measureName;
  private final String user;
  private final String password;

  public static InfluxDbConnectionSettings from(String influxDbHost,
                                                Integer influxDbPort,
                                                String databaseName,
                                                String measureName,
                                                String user,
                                                String password) {
    return new InfluxDbConnectionSettings(
            influxDbHost,
            influxDbPort,
            databaseName,
            measureName,
            user,
            password);
  }

  private InfluxDbConnectionSettings(String influxDbHost,
                                     Integer influxDbPort,
                                     String databaseName,
                                     String measureName,
                                     String user,
                                     String password) {
    this.influxDbHost = influxDbHost;
    this.influxDbPort = influxDbPort;
    this.databaseName = databaseName;
    this.measureName = measureName;
    this.user = user;
    this.password = password;
  }

  public Integer getInfluxDbPort() {
    return influxDbPort;
  }

  public String getInfluxDbHost() {
    return influxDbHost;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getMeasureName() {
    return measureName;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
