/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.message;

import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TsModel
@JsonSubTypes({
    @JsonSubTypes.Type(ErrorMessage.class),
    @JsonSubTypes.Type(SuccessMessage.class),
    @JsonSubTypes.Type(PipelineModificationMessage.class),
})
public abstract class Message {

  private boolean success;
  private String elementName;

  private List<Notification> notifications;

  public Message() {

  }

  public Message(boolean success) {
    this.success = success;
    this.notifications = null;
  }

  public Message(boolean success, List<Notification> notifications) {
    super();
    this.success = success;
    this.notifications = notifications;
  }

  public Message(boolean success, List<Notification> notifications, String elementName) {
    this(success, notifications);
    this.elementName = elementName;
  }


  public Message(boolean success, Notification... notifications) {
    this.success = success;
    this.notifications = new ArrayList<>();
    this.notifications.addAll(Arrays.asList(notifications));
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<Notification> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<Notification> notifications) {
    this.notifications = notifications;
  }

  public boolean addNotification(Notification notification) {
    return notifications.add(notification);
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }
}
