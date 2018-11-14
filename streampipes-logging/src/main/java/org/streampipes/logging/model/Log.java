package org.streampipes.logging.model;

public class Log {

  private String sourceID;
  private String timestamp;
  private String level;
  private String type;
  private String message;


  public String getsourceID() {
    return sourceID;
  }

  public void setsourceID(String sourceID) {
    this.sourceID = sourceID;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
