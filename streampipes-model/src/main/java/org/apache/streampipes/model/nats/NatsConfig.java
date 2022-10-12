package org.apache.streampipes.model.nats;

public class NatsConfig {

  private String natsUrls;
  private String subject;
  private String username;
  private String password;
  private String properties;

  public NatsConfig() {
  }

  public String getNatsUrls() {
    return natsUrls;
  }

  public void setNatsUrls(String natsUrls) {
    this.natsUrls = natsUrls;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getProperties() {
    return properties;
  }

  public void setProperties(String properties) {
    this.properties = properties;
  }
}
