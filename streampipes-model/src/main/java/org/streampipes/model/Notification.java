package org.streampipes.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {

  private @SerializedName("_id") String id;
  private @SerializedName("_rev") String rev;

  private String title;
  private Date createdAt;
  private String targetedAt;

  private String message;

  private Boolean read;

  public Notification(String title, Date createdAt, String targetedAt, String message) {
    this.title = title;
    this.createdAt = createdAt;
    this.targetedAt = targetedAt;
    this.message = message;
  }

  public Notification() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getTargetedAt() {
    return targetedAt;
  }

  public void setTargetedAt(String targetedAt) {
    this.targetedAt = targetedAt;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public Boolean isRead() {
    return read != null ? read : false;
  }

  public void setRead(Boolean read) {
    this.read = read;
  }
}
