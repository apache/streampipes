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
package org.apache.streampipes.model.connect.guess;

import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public class GuessSchema {

  public EventSchema eventSchema;
  public EventSchema targetSchema;
  private List<String> eventPreview;
  private List<TransformationRuleDescription> modifiedRules;
  public Map<String, FieldStatusInfo> fieldStatusInfo;

  // for adapter updates
  private List<EventProperty> removedProperties;
  private List<Notification> updateNotifications;

  public GuessSchema() {
    super();
    this.modifiedRules = new ArrayList<>();
    this.eventPreview = new ArrayList<>();
    this.fieldStatusInfo = new HashMap<>();
    this.removedProperties = new ArrayList<>();
    this.updateNotifications = new ArrayList<>();
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public void setEventSchema(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  public EventSchema getTargetSchema() {
    return targetSchema;
  }

  public void setTargetSchema(EventSchema targetSchema) {
    this.targetSchema = targetSchema;
  }

  public List<String> getEventPreview() {
    return eventPreview;
  }

  public void setEventPreview(List<String> eventPreview) {
    this.eventPreview = eventPreview;
  }

  public Map<String, FieldStatusInfo> getFieldStatusInfo() {
    return fieldStatusInfo;
  }

  public void setFieldStatusInfo(Map<String, FieldStatusInfo> fieldStatusInfo) {
    this.fieldStatusInfo = fieldStatusInfo;
  }

  public List<EventProperty> getRemovedProperties() {
    return removedProperties;
  }

  public void setRemovedProperties(List<EventProperty> removedProperties) {
    this.removedProperties = removedProperties;
  }

  public List<Notification> getUpdateNotifications() {
    return updateNotifications;
  }

  public void setUpdateNotifications(List<Notification> updateNotifications) {
    this.updateNotifications = updateNotifications;
  }

  public List<TransformationRuleDescription> getModifiedRules() {
    return modifiedRules;
  }

  public void setModifiedRules(List<TransformationRuleDescription> modifiedRules) {
    this.modifiedRules = modifiedRules;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GuessSchema that = (GuessSchema) o;
    return Objects.equals(eventSchema, that.eventSchema) && Objects.equals(eventPreview, that.eventPreview)
            && Objects.equals(fieldStatusInfo, that.fieldStatusInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventSchema, eventPreview, fieldStatusInfo);
  }

  @Override
  public String toString() {
    return "GuessSchema{" + "eventSchema=" + eventSchema + ", eventPreview=" + eventPreview + ", fieldStatusInfo="
            + fieldStatusInfo + '}';
  }
}
