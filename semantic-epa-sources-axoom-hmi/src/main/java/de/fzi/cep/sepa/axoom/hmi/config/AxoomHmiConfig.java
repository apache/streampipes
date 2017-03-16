package de.fzi.cep.sepa.axoom.hmi.config;

/**
 * Created by riemer on 16.03.2017.
 */
public enum AxoomHmiConfig {

  EUROBLECH("euroblech"),
  FABTECH("fabtech16"),
  HMI("hmi");

  private String eventType;
  private String topicPrefix = "axoom.hmi.";

  AxoomHmiConfig(String eventType) {
    this.eventType = eventType;
  }

  public String getTopic(String streamId) {
    return topicPrefix +eventType +"." +streamId;
  }

  public String getEventType() {
    return eventType;
  }
}
